package com.example.mobilefintechapp.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("com/example/mobilefintechapp/profile/forgot_password")

    object SignUpVerifyEmail : Screen("signup_verify_email/{email}/{fullName}/{password}") {
        fun createRoute(email: String, fullName: String, password: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            val encodedFullName = URLEncoder.encode(fullName, StandardCharsets.UTF_8.toString())
            val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString())
            return "signup_verify_email/$encodedEmail/$encodedFullName/$encodedPassword"
        }
    }

    object LoginVerifyEmail : Screen("login_verify_email/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "login_verify_email/$encodedEmail"
        }
    }

    object ForgotPasswordVerifyEmail : Screen("forgot_password_verify_email/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "forgot_password_verify_email/$encodedEmail"
        }
    }

    object ResetPassword : Screen("reset_password/{email}/{token}") {
        fun createRoute(email: String, token: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            val encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString())
            return "reset_password/$encodedEmail/$encodedToken"
        }
    }

    // Main App Screens
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Goals : Screen("goals")
    object Insights : Screen("insights")
    object Profile : Screen("profile")

    // Profile Settings Screens
    object VerifyPasswordForEmail : Screen("verify_password_for_email")
    object ChangeEmail : Screen("change_email/{password}") {
        fun createRoute(password: String): String {
            val encoded = URLEncoder.encode(password, StandardCharsets.UTF_8.toString())
            return "change_email/$encoded"
        }
    }
    object ChangeEmailVerify : Screen("change_email_verify/{newEmail}") {
        fun createRoute(newEmail: String): String {
            val encodedEmail = URLEncoder.encode(newEmail, StandardCharsets.UTF_8.toString())
            return "change_email_verify/$encodedEmail"
        }
    }
    object ChangePassword : Screen("change_password")
    object InsertEmail : Screen("insert_email")
}