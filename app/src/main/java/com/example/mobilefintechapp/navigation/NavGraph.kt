package com.example.mobilefintechapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mobilefintechapp.auth.forgot_password.ForgotPasswordScreen
import com.example.mobilefintechapp.auth.login.LoginScreen
import com.example.mobilefintechapp.auth.register.SignUpScreen
import com.example.mobilefintechapp.auth.forgot_password.ForgotPasswordScreen
import com.example.mobilefintechapp.auth.forgot_password.ForgotPasswordVerifyEmailScreen
import com.example.mobilefintechapp.auth.forgot_password.ResetPasswordScreen
import com.example.mobilefintechapp.auth.login.LoginVerifyOtpScreen
//import com.example.mobilefintechapp.auth.login.LoginVerifyEmailScreen
import com.example.mobilefintechapp.auth.register.SignUpScreen
//import com.example.mobilefintechapp.auth.login.LoginVerifyEmailScreen
import com.example.mobilefintechapp.auth.register.SignUpVerifyEmailScreen
import com.example.mobilefintechapp.goals.GoalDetailsScreen
import com.example.mobilefintechapp.goals.GoalsScreen
import com.example.mobilefintechapp.homepage.HomepageScreen
import com.example.mobilefintechapp.insights.InsightsScreen
import com.example.mobilefintechapp.profile.ProfileScreen
import com.example.mobilefintechapp.profile.change_email.ChangeEmailScreen
import com.example.mobilefintechapp.profile.change_email.ChangeEmailVerifyScreen
import com.example.mobilefintechapp.profile.change_email.VerifyPasswordScreen
import com.example.mobilefintechapp.profile.change_password.ChangePasswordScreen
import com.example.mobilefintechapp.profile.forgot_password.InsertEmailScreen
import com.example.mobilefintechapp.transactions.TransactionsScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Flow
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        // Sign Up Verify Email Screen
        composable(
            route = Screen.SignUpVerifyEmail.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("fullName") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val encodedFullName = backStackEntry.arguments?.getString("fullName") ?: ""
            val encodedPassword = backStackEntry.arguments?.getString("password") ?: ""

            // URL decode the parameters
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
            val fullName = URLDecoder.decode(encodedFullName, StandardCharsets.UTF_8.toString())
            val password = URLDecoder.decode(encodedPassword, StandardCharsets.UTF_8.toString())

            SignUpVerifyEmailScreen(
                navController = navController,
                userEmail = email,
                fullName = fullName,
                password = password
            )
        }

        // Login Verify Email Screen
        composable(
            route = Screen.LoginVerifyEmail.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())

            LoginVerifyOtpScreen(
                navController = navController,
                userEmail = email
            )
        }

        // Forgot Password Screen
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        // Forgot Password Verify Email Screen
        composable(
            route = Screen.ForgotPasswordVerifyEmail.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())

            ForgotPasswordVerifyEmailScreen(
                navController = navController,
                userEmail = email
            )
        }

        // Reset Password Screen
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }  // NEW: Token parameter
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val encodedToken = backStackEntry.arguments?.getString("token") ?: ""

            // URL decode the parameters
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
            val token = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8.toString())

            ResetPasswordScreen(
                navController = navController,
                userEmail = email,
                verificationToken = token  // NEW: Pass token to screen
            )
        }

        composable("goalDetails/{goalId}") { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            GoalDetailsScreen(
                navController = navController,
                goalId = goalId
            )
        }

        /*composable(route = Screen.LoginVerifyEmail.route) {
            LoginVerifyEmailScreen(navController = navController)
        }*/

        /*composable(route = Screen.ForgotPasswordVerifyEmail.route) {
            ForgotPasswordVerifyEmailScreen(navController = navController)
        }*/

        // Main App Screens
        composable(route = Screen.Dashboard.route) {
            HomepageScreen(navController = navController)
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(route = Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
            // TODO: Create TransactionsScreen
        }

        composable(route = Screen.Goals.route) {
            GoalsScreen(navController = navController)
            // TODO: Create GoalsScreen
        }

        composable(route = Screen.Insights.route) {
            InsightsScreen(navController = navController)
            // TODO: Create InsightsScreen
        }

        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
        }

        composable(route = Screen.VerifyPasswordForEmail.route) {
            VerifyPasswordScreen(navController = navController)
        }

        composable(
            route = Screen.ChangeEmail.route,
            arguments = listOf(navArgument("password") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPassword = backStackEntry.arguments?.getString("password") ?: ""
            val password = URLDecoder.decode(encodedPassword, StandardCharsets.UTF_8.toString())

            ChangeEmailScreen(navController = navController, userPassword = password)
        }

        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
        }

        composable(route = Screen.InsertEmail.route) {
            InsertEmailScreen(navController = navController)  // Your insertemail.kt composable
        }

        composable(
            route = Screen.ChangeEmailVerify.route,
            arguments = listOf(
                navArgument("newEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("newEmail") ?: ""
            val newEmail = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())

            ChangeEmailVerifyScreen(
                navController = navController,
                userEmail = newEmail
            )
        }
    }
}