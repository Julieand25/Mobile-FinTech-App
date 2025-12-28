package com.example.mobilefintechapp.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.navigation.Screen
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    navController: NavController,
    userEmail: String,
    verificationToken: String  // ✨ NEW: Receive token directly as parameter
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showPasswordMismatchError by remember { mutableStateOf(false) }
    var showWeakPasswordError by remember { mutableStateOf(false) }
    var isResetting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val functions = FirebaseFunctions.getInstance()

    // ✨ Debug log to verify token is received
    LaunchedEffect(Unit) {
        Log.d("ResetPasswordScreen", "Verification token received: $verificationToken")
    }

    // Check password match
    LaunchedEffect(newPassword, confirmPassword) {
        if (confirmPassword.isNotEmpty()) {
            showPasswordMismatchError = newPassword != confirmPassword
        } else {
            showPasswordMismatchError = false
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        ErrorDialog(
            errorMessage = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

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
            Spacer(modifier = Modifier.height(48.dp))

            // Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Set New Password",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enter your new account password",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Reset Password Card
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
                    // New Password Field
                    Text(
                        text = "New Password",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            showWeakPasswordError = it.isNotEmpty() && it.length < 6
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter new password") },
                        trailingIcon = {
                            if (newPassword.isNotEmpty()) {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (newPasswordVisible)
                                                R.drawable.ic_eye_open
                                            else
                                                R.drawable.ic_eye_closed
                                        ),
                                        contentDescription = if (newPasswordVisible)
                                            "Hide password"
                                        else
                                            "Show password",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        visualTransformation = if (newPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showWeakPasswordError) Color.Red else Color(0xFF10B981),
                            unfocusedBorderColor = if (showWeakPasswordError) Color.Red else Color.LightGray
                        ),
                        enabled = !isResetting,
                        isError = showWeakPasswordError
                    )

                    if (showWeakPasswordError) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Confirm New Password Field
                    Text(
                        text = "Confirm New Password",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirm new password") },
                        trailingIcon = {
                            if (confirmPassword.isNotEmpty()) {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (confirmPasswordVisible)
                                                R.drawable.ic_eye_open
                                            else
                                                R.drawable.ic_eye_closed
                                        ),
                                        contentDescription = if (confirmPasswordVisible)
                                            "Hide password"
                                        else
                                            "Show password",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showPasswordMismatchError) Color.Red else Color(0xFF10B981),
                            unfocusedBorderColor = if (showPasswordMismatchError) Color.Red else Color.LightGray
                        ),
                        enabled = !isResetting,
                        isError = showPasswordMismatchError
                    )

                    if (showPasswordMismatchError) {
                        Text(
                            text = "Passwords do not match",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.DarkGray
                            ),
                            enabled = !isResetting
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Confirm Button
                        Button(
                            onClick = {
                                // Validate inputs
                                if (newPassword.length < 6) {
                                    showWeakPasswordError = true
                                    return@Button
                                }

                                if (newPassword != confirmPassword) {
                                    showPasswordMismatchError = true
                                    return@Button
                                }

                                if (verificationToken == null) {
                                    errorMessage = "Verification token missing. Please restart the process."
                                    showErrorDialog = true
                                    return@Button
                                }

                                // Call Cloud Function to reset password
                                isResetting = true
                                scope.launch {
                                    try {
                                        val data = hashMapOf(
                                            "email" to userEmail,
                                            "newPassword" to newPassword,
                                            "otpVerificationToken" to verificationToken
                                        )

                                        val result = functions
                                            .getHttpsCallable("resetUserPassword")
                                            .call(data)
                                            .await()

                                        // Success!
                                        isResetting = false
                                        showSuccessDialog = true

                                    } catch (e: Exception) {
                                        isResetting = false
                                        errorMessage = e.message ?: "Failed to reset password"
                                        showErrorDialog = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            ),
                            enabled = newPassword.isNotEmpty() &&
                                    confirmPassword.isNotEmpty() &&
                                    newPassword == confirmPassword &&
                                    newPassword.length >= 6 &&
                                    !isResetting
                        ) {
                            if (isResetting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Confirm",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Password Reset Successfully!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your password has been updated. You can now login with your new password.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    )
                ) {
                    Text(
                        text = "Go to Login",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = Color(0xFF10B981))
            }
        }
    )
}