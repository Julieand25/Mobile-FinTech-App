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
//import com.example.mobilefintechapp.auth.login.LoginVerifyEmailScreen
import com.example.mobilefintechapp.auth.register.SignUpScreen
//import com.example.mobilefintechapp.auth.login.LoginVerifyEmailScreen
import com.example.mobilefintechapp.auth.register.SignUpVerifyEmailScreen
import com.example.mobilefintechapp.homepage.HomepageScreen
import com.example.mobilefintechapp.profile.ProfileScreen
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

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        /*composable(route = Screen.LoginVerifyEmail.route) {
            LoginVerifyEmailScreen(navController = navController)
        }*/

        composable(route = Screen.ForgotPasswordVerifyEmail.route) {
            ForgotPasswordVerifyEmailScreen(navController = navController)
        }

        // Main App Screens
        composable(route = Screen.Dashboard.route) {
            HomepageScreen(navController = navController)
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(route = Screen.Transactions.route) {
            // TransactionsScreen(navController = navController)
            // TODO: Create TransactionsScreen
        }

        composable(route = Screen.Goals.route) {
            // GoalsScreen(navController = navController)
            // TODO: Create GoalsScreen
        }

        composable(route = Screen.Insights.route) {
            // InsightsScreen(navController = navController)
            // TODO: Create InsightsScreen
        }
    }
}