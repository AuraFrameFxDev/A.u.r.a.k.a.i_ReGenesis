package dev.aurakai.auraframefx.domains.aura.ui

enum class ElementType {
    QUICK_SETTINGS,
    LOCK_SCREEN,
    NOTIFICATION,
    STATUS_BAR,
    APP_DRAWER,
    LAUNCHER,
    SYSTEM_UI,
    APP_OVERLAY,

    // Adding placeholders based on subtask prompt for SystemOverlayManager interface,
    // even if not directly used in Impl's applyElement's when statement.
    TEXT,
    IMAGE,
    SHAPE // Already covered by existing model types, but good as a generic type
}

