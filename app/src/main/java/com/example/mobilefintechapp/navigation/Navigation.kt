package com.example.mobilefintechapp.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("forgot_password")
    object SignUpVerifyEmail : Screen("signup_verify_email/{email}/{fullName}/{password}") {
        fun createRoute(email: String, fullName: String, password: String): String {
            // URL encode the parameters to handle special characters
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            val encodedFullName = URLEncoder.encode(fullName, StandardCharsets.UTF_8.toString())
            val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString())
            return "signup_verify_email/$encodedEmail/$encodedFullName/$encodedPassword"
        }
    }
    object LoginVerifyEmail : Screen("login_verify_email")
    object ForgotPasswordVerifyEmail : Screen("forgot_password_verify_email")

    // Main App Screens
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Goals : Screen("goals")
    object Insights : Screen("insights")
    object Profile : Screen("profile")
}