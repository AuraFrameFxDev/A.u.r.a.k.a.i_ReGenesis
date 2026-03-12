package dev.aurakai.auraframefx.domains.aura.ui

/**
 * String resources for the AuraFrameFX app
 * Using Kotlin object instead of strings.xml for better integration with Compose
 */
object AppStrings {
    // App Information
    const val APP_NAME = "AuraFrameFX"
    const val APP_DESCRIPTION = "AuraFrameFX hooks and enhancements"

    // Common UI Elements
    const val BUTTON_CONTINUE = "Continue"
    const val BUTTON_CANCEL = "Cancel"
    const val BUTTON_OK = "OK"
    const val BUTTON_SUBMIT = "Submit"
    const val BUTTON_NEXT = "Next"
    const val BUTTON_PREVIOUS = "Previous"

    // Navigation
    const val NAV_HOME = "Home"
    const val NAV_PROFILE = "Profile"
    const val NAV_SETTINGS = "Settings"
    const val NAV_AI_CHAT = "AI Chat"

    // Feature Specific Text
    const val AI_CHAT_PLACEHOLDER = "Type your message here..."
    const val AI_CHAT_SEND = "Send"
    const val AI_CHAT_CLEAR = "Clear Chat"

    // Settings
    const val SETTINGS_THEME = "App Theme"
    const val SETTINGS_NOTIFICATIONS = "Notifications"
    const val SETTINGS_PRIVACY = "Privacy"

    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_GENERAL = "Something went wrong. Please try again."
}

