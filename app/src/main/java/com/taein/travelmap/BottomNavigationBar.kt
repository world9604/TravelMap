package com.taein.travelmap

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object PhotoMap : BottomNavItem(
        route = Destination.Map.route,
        icon = Icons.Default.Map,
        title = "Photo Map"
    )

    object Timeline : BottomNavItem(
        route = "Timeline",
        icon = Icons.Default.Timeline,
        title = "Timeline"
    )

    object Gallery : BottomNavItem(
        route = "Gallery",
        icon = Icons.Default.Person,
        title = "Gallery"
    )
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.PhotoMap,
        BottomNavItem.Timeline,
        BottomNavItem.Gallery
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}
