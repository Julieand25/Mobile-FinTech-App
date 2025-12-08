package com.example.mobilefintechapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.homepage.HalalFinanceTheme
import com.example.mobilefintechapp.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HalalFinanceTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}