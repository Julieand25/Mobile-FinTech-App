package com.example.mobilefintechapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.homepage.HalalFinanceTheme
import com.example.mobilefintechapp.navigation.NavGraph
import com.example.mobilefintechapp.navigation.Screen
import com.example.mobilefintechapp.viewmodel.BankLinkingViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val bankLinkingViewModel: BankLinkingViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    // Register permission launcher for notifications (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "✅ Notification permission granted")
            // Get FCM token after permission granted
            getFCMToken()
        } else {
            Log.d(TAG, "❌ Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Request notification permission for Android 13+ (API 33+)
        requestNotificationPermission()

        // Get FCM token
        getFCMToken()

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

        // Check if opened from notification
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //handleIntent(intent)
        handleNotificationIntent(intent)
    }

    /**
     * Request notification permission for Android 13+ (Tiramisu)
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "✅ Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show explanation to user why notification permission is needed
                    Log.d(TAG, "ℹ️ Should show permission rationale")
                    // You can show a dialog here explaining why notifications are important
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For Android 12 and below, notification permission is granted by default
            Log.d(TAG, "✅ Notification permission not required for this Android version")
        }
    }

    /**
     * Get FCM token and save to Firestore
     */
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "❌ Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "📱 FCM Token: $token")

            // Save token to Firestore
            saveTokenToFirestore(token)
        }
    }

    /**
     * Save FCM token to Firestore for the current user
     */
    private fun saveTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "⚠️ User not logged in, cannot save FCM token")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(TAG, "✅ FCM token saved to Firestore for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error saving FCM token to Firestore", e)

                // If update fails (user document might not exist), try to set it
                db.collection("users")
                    .document(userId)
                    .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ FCM token created in Firestore for user: $userId")
                    }
                    .addOnFailureListener { e2 ->
                        Log.e(TAG, "❌ Error creating FCM token in Firestore", e2)
                    }
            }
    }

    /**
     * Handle notification tap - navigate to Insights screen
     */
    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val navigateTo = it.getStringExtra("navigate_to")
            val notificationType = it.getStringExtra("notification_type")
            val notificationCategory = it.getStringExtra("notification_category")

            if (navigateTo == "insights") {
                Log.d(TAG, "📱 Opened from notification: Type=$notificationType, Category=$notificationCategory")
                // TODO: Navigate to Insights screen
                // You can pass these extras to your navigation system
                // For now, just log them
            }
        }
    }

    /*
    // Uncomment if you need OAuth handling
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
    }
    */
}