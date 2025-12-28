package com.example.mobilefintechapp.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilefintechapp.navigation.Screen
import com.example.mobilefintechapp.viewmodel.OtpViewModel
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    otpViewModel: OtpViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isChecking by remember { mutableStateOf(false) }

    val firestore = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF10B881),
                        Color(0xFF0E9788)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title and Subtitle
                Column {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Enter your email to reset password",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Forgot Password Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Email Field
                    Text(
                        text = "Email Address",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (it.isNotEmpty()) {
                                showEmailError = false
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear email",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showEmailError) Color.Red else Color(0xFF10B981),
                            unfocusedBorderColor = if (showEmailError) Color.Red else Color.LightGray
                        ),
                        isError = showEmailError,
                        enabled = !isChecking
                    )

                    // Error message
                    if (showEmailError && errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Next Button
                    Button(
                        onClick = {
                            if (email.isEmpty()) {
                                showEmailError = true
                                errorMessage = "Please enter your email"
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                showEmailError = true
                                errorMessage = "Please enter a valid email"
                            } else {
                                // Check if email exists in Firestore users collection
                                isChecking = true

                                firestore.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        isChecking = false
                                        if (!documents.isEmpty) {
                                            // Email exists, send OTP and navigate
                                            val route = Screen.ForgotPasswordVerifyEmail.createRoute(email)
                                            navController.navigate(route)
                                        } else {
                                            // Email doesn't exist
                                            showEmailError = true
                                            errorMessage = "No account found with this email"
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        isChecking = false
                                        showEmailError = true
                                        errorMessage = "Failed to verify email. Please try again."
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        enabled = !isChecking
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Next",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}