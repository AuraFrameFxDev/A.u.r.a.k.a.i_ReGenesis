// cmd/sentinel-daemon/main.go
package main

import (
	"bytes"
	"context"
	"encoding/binary"
	"errors"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/cilium/ebpf"
	"github.com/cilium/ebpf/link"
	"github.com/cilium/ebpf/ringbuf"
	"github.com/cilium/ebpf/rlimit"
)

//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -target bpfel -cc clang Sentinel ./bpf/sentinel.bpf.c -- -I./bpf/headers

func main() {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	log.Println("🛡️ Kai Sentinel Daemon v0.1 – Kernel Heartbeat initializing...")

	// Bump RLIMIT_MEMLOCK (required for eBPF maps)
	if err := rlimit.RemoveMemlock(); err != nil {
		log.Fatalf("Failed to raise memlock rlimit: %v", err)
	}

	// Load pre-compiled eBPF objects
	objs := SentinelObjects{}
	if err := LoadSentinelObjects(&objs, nil); err != nil {
		log.Fatalf("Failed to load eBPF objects: %v", err)
	}
	defer objs.Close()

	// Attach to sys_enter_execve tracepoint
	tpLink, err := link.Tracepoint("syscalls", "sys_enter_execve", objs.ExecveEnter, nil)
	if err != nil {
		log.Fatalf("Failed to attach sys_enter_execve tracepoint: %v", err)
	}
	defer tpLink.Close()

	log.Println("✅ Attached to sys_enter_execve. Capturing every process spawn...")

	// Ringbuf reader – streams events from kernel
	reader, err := ringbuf.NewReader(objs.Events)
	if err != nil {
		log.Fatalf("Failed to open ringbuf: %v", err)
	}
	defer reader.Close()

	// Context for clean shutdown
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Signal handler
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-sigChan
		log.Println("🛑 Received shutdown signal – retracting probes...")
		cancel()
	}()

	// Event structures must match the C code
	type bpfEvent struct {
		Pid   uint32
		PPid  uint32
		Comm  [16]byte
		Fname [256]byte
	}

	// Event processing loop
	for {
		select {
		case <-ctx.Done():
			log.Println("Daemon shutdown complete.")
			return
		default:
			record, err := reader.Read()
			if err != nil {
				if errors.Is(err, ringbuf.ErrClosed) {
					return
				}
				log.Printf("Ringbuf read error: %v", err)
				continue
			}

			// Parse the event from raw sample
			var event struct {
				Pid   uint32
				PPid  uint32
				Comm  [16]byte
				Fname [256]byte
			}
			if err := binary.Read(bytes.NewReader(record.RawSample), binary.LittleEndian, &event); err != nil {
				log.Printf("Failed to parse event: %v", err)
				continue
			}

			// Trim null bytes and log
			commStr := string(bytes.TrimRight(event.Comm[:], "\x00"))
			fnameStr := string(bytes.TrimRight(event.Fname[:], "\x00"))
			log.Printf("[EXECVE] PID=%d PPID=%d Comm=%s Path=%s", event.Pid, event.PPid, commStr, fnameStr)

			// TODO: Marshal to JSON → send over UNIX socket to Cascade router
			// e.g. jsonEvent := json.Marshal(event); sendToCascade(jsonEvent)
		}
	}
}
