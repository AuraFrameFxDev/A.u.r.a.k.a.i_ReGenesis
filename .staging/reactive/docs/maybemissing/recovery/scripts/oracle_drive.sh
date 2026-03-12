#!/system/bin/sh
# ═══════════════════════════════════════════════════════════════════
# 🔮 ORACLE DRIVE - Recovery Terminal Interface
# ═══════════════════════════════════════════════════════════════════
#
# Recovery-accessible command-line interface for Oracle Drive
# Can be run from TWRP terminal, adb shell in recovery, or flashable ZIP
#
# Usage:
#   oracle_drive.sh [command] [options]
#
# Commands:
#   bootloader  - Check bootloader unlock status
#   root        - Manage root access (detect, install, configure)
#   backup      - Backup system partitions
#   restore     - Restore from backup
#   flash       - Flash ROM/kernel/modules
#   info        - Display device information
#   menu        - Launch interactive menu
#
# Environment: Recovery mode (TWRP/LineageOS Recovery)
# ═══════════════════════════════════════════════════════════════════

set -e

# Recovery environment detection
if [ -z "$RECOVERY" ]; then
    export RECOVERY=1
fi

# Oracle Drive paths
ORACLE_DIR="/data/adb/oracle_drive"
ORACLE_BIN="$ORACLE_DIR/bin"
ORACLE_LOG="$ORACLE_DIR/recovery.log"
ORACLE_BACKUP="$ORACLE_DIR/backup"

# Color codes for terminal
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ═══════════════════════════════════════════════════════════════════
# Logging Functions
# ═══════════════════════════════════════════════════════════════════

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$ORACLE_LOG"
}

log_info() {
    echo -e "${CYAN}[INFO]${NC} $1" | tee -a "$ORACLE_LOG"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a "$ORACLE_LOG"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$ORACLE_LOG"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$ORACLE_LOG"
}

# ═══════════════════════════════════════════════════════════════════
# Banner
# ═══════════════════════════════════════════════════════════════════

show_banner() {
    echo -e "${MAGENTA}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                                                              ║"
    echo "║              🔮 ORACLE DRIVE - RECOVERY MODE 🔮              ║"
    echo "║                                                              ║"
    echo "║         AI-Powered Universal Root Management System         ║"
    echo "║                                                              ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# ═══════════════════════════════════════════════════════════════════
# Bootloader Management
# ═══════════════════════════════════════════════════════════════════

check_bootloader() {
    log_info "Checking bootloader unlock status..."

    # Get bootloader state
    local verified_boot=$(getprop ro.boot.verifiedbootstate)
    local warranty_bit=$(getprop ro.boot.warranty_bit)
    local lock_state=$(getprop ro.secureboot.lockstate)
    local flash_locked=$(getprop ro.boot.flash.locked)

    echo -e "\n${CYAN}═══ Bootloader Status ═══${NC}"
    echo "Verified Boot State: $verified_boot"
    echo "Warranty Bit: $warranty_bit"
    echo "Lock State: $lock_state"
    echo "Flash Locked: $flash_locked"

    # Determine if unlocked
    if [ "$verified_boot" = "orange" ] || [ "$verified_boot" = "red" ] || \
       [ "$warranty_bit" = "1" ] || [ "$lock_state" = "unlocked" ] || \
       [ "$flash_locked" = "0" ]; then
        log_success "Bootloader is UNLOCKED"
        echo -e "${GREEN}✓ Bootloader is UNLOCKED${NC}"
        return 0
    else
        log_warn "Bootloader is LOCKED"
        echo -e "${RED}✗ Bootloader is LOCKED${NC}"
        echo -e "${YELLOW}Oracle Drive requires unlocked bootloader for root access.${NC}"
        return 1
    fi
}

# ═══════════════════════════════════════════════════════════════════
# Root Detection & Management
# ═══════════════════════════════════════════════════════════════════

detect_root() {
    log_info "Detecting root solutions..."

    local root_method="none"

    # Check for APatch
    if [ -f "/data/adb/apd" ] || command -v apd >/dev/null 2>&1; then
        root_method="APatch"
        log_success "APatch detected"
    # Check for KernelSU
    elif [ -f "/data/adb/ksud" ] || command -v ksud >/dev/null 2>&1; then
        root_method="KernelSU"
        log_success "KernelSU detected"
    # Check for Magisk
    elif [ -f "/data/adb/magisk/magisk" ] || command -v magisk >/dev/null 2>&1; then
        root_method="Magisk"
        log_success "Magisk detected"
    else
        log_warn "No root solution detected"
    fi

    echo -e "\n${CYAN}═══ Root Status ═══${NC}"
    echo "Root Method: $root_method"

    if [ "$root_method" != "none" ]; then
        echo -e "${GREEN}✓ Device is rooted with $root_method${NC}"
        return 0
    else
        echo -e "${YELLOW}✗ No root solution found${NC}"
        return 1
    fi
}

install_root() {
    local method="$1"

    log_info "Installing $method..."

    case "$method" in
        "apatch"|"APatch")
            echo -e "${CYAN}Installing APatch...${NC}"
            # APatch installation logic would go here
            log_warn "APatch installation not yet implemented in recovery"
            echo -e "${YELLOW}Please use APatch Manager app after boot${NC}"
            ;;
        "magisk"|"Magisk")
            echo -e "${CYAN}Installing Magisk...${NC}"
            # Magisk installation logic would go here
            log_warn "Magisk installation not yet implemented in recovery"
            echo -e "${YELLOW}Please flash Magisk ZIP from recovery${NC}"
            ;;
        "kernelsu"|"KernelSU")
            echo -e "${CYAN}Installing KernelSU...${NC}"
            log_warn "KernelSU requires custom kernel"
            echo -e "${YELLOW}Please flash KernelSU-patched kernel${NC}"
            ;;
        *)
            log_error "Unknown root method: $method"
            return 1
            ;;
    esac
}

# ═══════════════════════════════════════════════════════════════════
# Device Information
# ═══════════════════════════════════════════════════════════════════

show_device_info() {
    log_info "Gathering device information..."

    echo -e "\n${CYAN}═══ Device Information ═══${NC}"
    echo "Manufacturer: $(getprop ro.product.manufacturer)"
    echo "Model: $(getprop ro.product.model)"
    echo "Device: $(getprop ro.product.device)"
    echo "Android Version: $(getprop ro.build.version.release)"
    echo "SDK Version: $(getprop ro.build.version.sdk)"
    echo "Build ID: $(getprop ro.build.id)"
    echo "Fingerprint: $(getprop ro.build.fingerprint)"

    echo -e "\n${CYAN}═══ Partition Information ═══${NC}"
    df -h | grep -E "(system|vendor|product|boot|data)" || true

    echo -e "\n${CYAN}═══ Security Status ═══${NC}"
    echo "SELinux: $(getenforce 2>/dev/null || echo 'Unknown')"
    echo "Verified Boot: $(getprop ro.boot.verifiedbootstate)"
}

# ═══════════════════════════════════════════════════════════════════
# Main Command Handler
# ═══════════════════════════════════════════════════════════════════

main() {
    # Ensure Oracle Drive directory exists
    mkdir -p "$ORACLE_DIR" "$ORACLE_BIN" "$ORACLE_BACKUP"

    # Show banner
    show_banner

    # Parse command
    local command="${1:-menu}"

    case "$command" in
        "bootloader"|"bl")
            check_bootloader
            ;;
        "root")
            detect_root
            ;;
        "install-root")
            install_root "$2"
            ;;
        "info"|"device")
            show_device_info
            ;;
        "menu")
            # Launch interactive menu
            if [ -f "$ORACLE_BIN/recovery_menu.sh" ]; then
                sh "$ORACLE_BIN/recovery_menu.sh"
            else
                log_error "Recovery menu not found"
                echo "Available commands: bootloader, root, info"
            fi
            ;;
        "help"|"-h"|"--help")
            echo "Oracle Drive - Recovery Terminal Interface"
            echo ""
            echo "Usage: oracle_drive.sh [command]"
            echo ""
            echo "Commands:"
            echo "  bootloader    - Check bootloader unlock status"
            echo "  root          - Detect root solution"
            echo "  install-root  - Install root solution (apatch/magisk/kernelsu)"
            echo "  info          - Show device information"
            echo "  menu          - Launch interactive menu"
            echo "  help          - Show this help message"
            ;;
        *)
            log_error "Unknown command: $command"
            echo "Run 'oracle_drive.sh help' for usage information"
            exit 1
            ;;
    esac
}

# Run main
main "$@"
