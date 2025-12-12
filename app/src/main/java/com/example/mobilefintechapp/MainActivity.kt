package com.example.mobilefintechapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.homepage.HalalFinanceTheme
import com.example.mobilefintechapp.navigation.NavGraph
import com.example.mobilefintechapp.navigation.Screen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            HalalFinanceTheme {
                val navController = rememberNavController()

                // Check if user is already logged in
                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDestination = if (currentUser != null) {
                    // User is logged in, go to dashboard
                    Screen.Dashboard.route
                } else {
                    // User is not logged in, go to login screen
                    Screen.Login.route
                }

                NavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}