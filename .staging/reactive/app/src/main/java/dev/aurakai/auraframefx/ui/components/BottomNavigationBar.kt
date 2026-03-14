package dev.aurakai.auraframefx.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.aurakai.auraframefx.ui.navigation.NavDestination

/**
 * Bottom navigation bar for the AuraFrameFX app
 */
/**
 * Displays a bottom navigation bar with navigation items for the main app destinations.
 *
 * The navigation bar highlights the currently selected destination and allows users to switch between destinations.
 * Navigation actions preserve and restore state, avoid duplicate destinations in the back stack, and pop up to the home route as needed.
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param navController Controller used to manage app navigation and determine the current route.
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavDestination.bottomNavItems.forEach { destination ->
            NavigationBarItem(
                icon = {
                    destination.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = destination.title
                        )
                    }
                },
                label = {
                    Text(
                        text = destination.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = currentRoute == destination.route,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(NavDestination.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when navigating to a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
