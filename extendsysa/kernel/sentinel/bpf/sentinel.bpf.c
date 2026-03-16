#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_tracing.h>

#define MAX_COMM_LEN 16
#define MAX_PATH_LEN 256

struct event {
    __u32 pid;
    __u32 ppid;
    char comm[MAX_COMM_LEN];
    char fname[MAX_PATH_LEN];
};

struct {
    __uint(type, BPF_MAP_TYPE_RINGBUF);
    __uint(max_entries, 1 << 24); // 16MB ringbuf
} events SEC(".maps");

SEC("tracepoint/syscalls/sys_enter_execve")
int trace_execve(struct trace_event_raw_sys_enter *ctx)
{
    struct event *e;
    u32 pid = bpf_get_current_pid_tgid() >> 32;

    e = bpf_ringbuf_reserve(&events, sizeof(*e), 0);
    if (!e)
        return 0;

    e->pid = pid;
    // Note: For real ppid we'd need task_struct from bpf_get_current_task()
    // For now, mirroring pid to match Grok's skeleton
    e->ppid = bpf_get_current_pid_tgid() >> 32; 
    
    bpf_get_current_comm(&e->comm, sizeof(e->comm));

    // Read filename arg (ctx+args[0])
    bpf_probe_read_user_str(&e->fname, sizeof(e->fname), (void *)ctx->args[0]);

    bpf_ringbuf_submit(e, 0);
    return 0;
}

char _license[] SEC("license") = "GPL";
