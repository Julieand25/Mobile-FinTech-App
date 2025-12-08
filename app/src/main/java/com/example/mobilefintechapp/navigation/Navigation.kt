package com.example.mobilefintechapp.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("com/example/mobilefintechapp/transactions")
    object Goals : Screen("goals")
    object Insights : Screen("insights")
    object Profile : Screen("com/example/mobilefintechapp/profile")
}