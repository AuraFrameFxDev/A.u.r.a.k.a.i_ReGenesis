package dev.aurakai.auraframefx.navigation

/**
 * Navigation routes for the app
 */
object Routes {
    const val Home = "home"
    const val AIContent = "ai_content"
    const val ConferenceRoom = "conference_room"
    const val Diagnostics = "diagnostics"
    const val OracleDrive = "oracle_drive"
    const val SystemCustomization = "system_customization"
}

/**
 * Bottom navigation items
 */
data class BottomNavItem(
    val title: String,
    val icon: Int,
    val route: String,
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        icon = android.R.drawable.ic_dialog_info,
        route = Routes.Home
    ),
    BottomNavItem(
        title = "AI",
        icon = android.R.drawable.ic_dialog_info,
        route = Routes.AIContent
    ),
    BottomNavItem(
        title = "Conference",
        icon = android.R.drawable.ic_dialog_info,
        route = Routes.ConferenceRoom
    ),
    BottomNavItem(
        title = "Diagnostics",
        icon = android.R.drawable.ic_menu_info_details,
        route = Routes.Diagnostics
    )
)
