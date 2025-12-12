package com.example.mobilefintechapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions

class MyApplication : Application() {

    companion object {
        private const val TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "==========================================")
        Log.d(TAG, "Application starting...")
        Log.d(TAG, "==========================================")

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "✅ Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize Firebase", e)
        }

        // Get Firebase Functions instance and log
        try {
            val functions = FirebaseFunctions.getInstance()
            Log.d(TAG, "✅ Firebase Functions instance created")
            //Log.d(TAG, "Functions region: ${functions.app.name}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get Firebase Functions instance", e)
        }

        Log.d(TAG, "==========================================")
        Log.d(TAG, "Application initialization complete")
        Log.d(TAG, "==========================================")
    }
}