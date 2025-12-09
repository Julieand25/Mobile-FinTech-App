package com.example.mobilefintechapp.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("com/example/mobilefintechapp/transactions")
    object Goals : Screen("com/example/mobilefintechapp/goals")
    object Insights : Screen("com/example/mobilefintechapp/insights")
    object Profile : Screen("com/example/mobilefintechapp/profile")
}