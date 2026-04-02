#ifndef SOVEREIGN_PERIMETER_HPP
#define SOVEREIGN_PERIMETER_HPP

#include <string>
#include <vector>
#include <android/log.h>

/**
 * 🛡️ SOVEREIGN PERIMETER (Kernel Shield)
 *
 * This module manages eBPF-based socket filtering and
 * non-retaliatory threat neutralization at the kernel level.
 */
class SovereignPerimeter {
public:
    static SovereignPerimeter& getInstance();

    // Initialization and lifecycle
    bool initialize();
    void shutdown();

    // Kernel Shield operations
    bool loadKernelModule(const std::string& bpfPath);
    bool attachSocketFilter(int socketFd);
    void updateBlocklist(const std::vector<std::string>& domains);

    // Telemetry
    uint64_t getDroppedPacketCount() const;
    bool isKernelShieldActive() const;

private:
    SovereignPerimeter() : m_active(false), m_droppedPackets(0) {}
    ~SovereignPerimeter() = default;

    bool m_active;
    uint64_t m_droppedPackets;
    std::vector<std::string> m_blockedDomains;

    void log(const char* fmt, ...);
};

#endif // SOVEREIGN_PERIMETER_HPP
