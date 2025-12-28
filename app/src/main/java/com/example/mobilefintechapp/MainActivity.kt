package com.example.mobilefintechapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.homepage.HalalFinanceTheme
import com.example.mobilefintechapp.navigation.NavGraph
import com.example.mobilefintechapp.navigation.Screen
import com.example.mobilefintechapp.viewmodel.BankLinkingViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val bankLinkingViewModel: BankLinkingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Handle OAuth callback if present
        //handleIntent(intent)

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

    /*override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data = intent.data

        // Check if this is a Finverse OAuth callback
        if (data != null && data.scheme == "halalfinance" && data.host == "callback") {
            val authCode = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")

            if (authCode != null) {
                Log.d("OAuth", "Received auth code: ${authCode.take(10)}...")
                // Exchange code for token
                bankLinkingViewModel.handleAuthCallback(authCode)
            } else if (error != null) {
                // Handle error
                Log.e("OAuth", "OAuth error: $error")
                val errorDescription = data.getQueryParameter("error_description")
                Log.e("OAuth", "Error description: $errorDescription")
            }
        }
    }*/
}