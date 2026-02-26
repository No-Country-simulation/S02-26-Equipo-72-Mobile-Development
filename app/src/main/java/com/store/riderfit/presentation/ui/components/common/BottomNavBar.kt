package com.store.riderfit.presentation.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.store.riderfit.presentation.ui.navigation.Route
import com.store.riderfit.presentation.ui.theme.RiderFitColors

private data class BottomItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavController) {
    val menuItems = listOf(
        BottomItem(Route.Home.route, "Armario", Icons.Default.Home),
        BottomItem(Route.Search.route, "Explorar", Icons.Default.Search),
        BottomItem(Route.Profile.route, "Mi perfil", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = RiderFitColors.PrimaryTones.L800,
        contentColor = RiderFitColors.PrimaryTones.L100
    ) {
        menuItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) RiderFitColors.PrimaryTones.L200
                        else RiderFitColors.PrimaryTones.L100.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) RiderFitColors.PrimaryTones.L200
                        else RiderFitColors.PrimaryTones.L100.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = RiderFitColors.PrimaryTones.L700
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    val navController = rememberNavController()
    BottomNavBar(navController = navController)
}