package com.example.mobilefintechapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobilefintechapp.homepage.HomepageScreen
import com.example.mobilefintechapp.profile.ProfileScreen

// Import your other screens here when you create them
// import com.example.mobilefintechapp.transactions.TransactionsScreen
// import com.example.mobilefintechapp.goals.GoalsScreen
// etc.

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(route = Screen.Dashboard.route) {
            HomepageScreen(navController = navController)
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        /*composable(route = Screen.Transactions.route) {
            // TransactionsScreen(navController = navController)
            // Temporary placeholder until you create the screen
            PlaceholderScreen(screenName = "Transactions", navController = navController)
        }

        composable(route = Screen.Goals.route) {
            // GoalsScreen(navController = navController)
            PlaceholderScreen(screenName = "Goals", navController = navController)
        }

        composable(route = Screen.Insights.route) {
            // InsightsScreen(navController = navController)
            PlaceholderScreen(screenName = "Insights", navController = navController)
        }

        composable(route = Screen.Profile.route) {
            // ProfileScreen(navController = navController)
            PlaceholderScreen(screenName = "Profile", navController = navController)
        }*/
    }
}

// Temporary placeholder composable
/*@Composable
fun PlaceholderScreen(screenName: String, navController: NavHostController) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$screenName Screen (Coming Soon)",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }

    // Add bottom navigation
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        com.example.mobilefintechapp.components.BottomNavigationBar(
            navController = navController,
            modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        )
    }
}*/