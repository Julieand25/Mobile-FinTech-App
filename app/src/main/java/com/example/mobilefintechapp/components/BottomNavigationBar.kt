package com.example.mobilefintechapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Dashboard
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Dashboard.route)
                            R.drawable.home_highlight
                        else
                            R.drawable.home_grey
                    ),
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Dashboard", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF10B881),
                selectedTextColor = Color(0xFF10B881),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Transactions
        NavigationBarItem(
            selected = currentRoute == Screen.Transactions.route,
            onClick = {
                navController.navigate(Screen.Transactions.route) {
                    popUpTo(Screen.Dashboard.route)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Transactions.route)
                            R.drawable.transcations_highlight
                        else
                            R.drawable.transcations_grey
                    ),
                    contentDescription = "Transactions",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Transactions", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF10B881),
                selectedTextColor = Color(0xFF10B881),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Goals
        NavigationBarItem(
            selected = currentRoute == Screen.Goals.route,
            onClick = {
                navController.navigate(Screen.Goals.route) {
                    popUpTo(Screen.Dashboard.route)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Goals.route)
                            R.drawable.dart_board_highlight
                        else
                            R.drawable.dart_board_grey
                    ),
                    contentDescription = "Goals",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Goals", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF10B881),
                selectedTextColor = Color(0xFF10B881),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Insights
        NavigationBarItem(
            selected = currentRoute == Screen.Insights.route,
            onClick = {
                navController.navigate(Screen.Insights.route) {
                    popUpTo(Screen.Dashboard.route)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Insights.route)
                            R.drawable.notification_highlight
                        else
                            R.drawable.notification_grey
                    ),
                    contentDescription = "Insights",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Insights", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF10B881),
                selectedTextColor = Color(0xFF10B881),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Profile
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(Screen.Dashboard.route)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == Screen.Profile.route)
                            R.drawable.profile_highlight
                        else
                            R.drawable.profile_grey
                    ),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Profile", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF10B881),
                selectedTextColor = Color(0xFF10B881),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}