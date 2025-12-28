package com.example.mobilefintechapp.auth.forgot_password

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilefintechapp.data.model.OtpType
import com.example.mobilefintechapp.navigation.Screen
import com.example.mobilefintechapp.viewmodel.OtpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordVerifyEmailScreen(
    navController: NavController,
    userEmail: String,
    otpViewModel: OtpViewModel = viewModel()
) {
    val otpUiState by otpViewModel.uiState.collectAsStateWithLifecycle()

    // Initialize with FORGOT_PASSWORD type
    LaunchedEffect(userEmail) {
        otpViewModel.initializeOtp(
            email = userEmail,
            otpType = OtpType.FORGOT_PASSWORD
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
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section with Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
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

                // Title
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.7f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verify Email",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Enter the 6-digit code sent to your email",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verification Card
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
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Verify Your Email",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle with email
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Enter the 6-digit code sent to",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Attempts remaining
                    if (otpUiState.attemptsLeft < 5 && otpUiState.attemptsLeft > 0) {
                        Text(
                            text = "${otpUiState.attemptsLeft} attempts remaining",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP Input Boxes
                    ForgotPasswordOTPInputField(
                        otpValues = otpUiState.otpValues,
                        onOtpChange = { newValues ->
                            otpViewModel.updateOtpValues(newValues)
                        },
                        enabled = !otpUiState.isLocked && !otpUiState.isVerifying
                    )

                    // Error Message
                    if (otpUiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = otpUiState.errorMessage!!,
                            color = Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Resend Code Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Didn't receive the code?",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (otpUiState.isLocked) {
                            Text(
                                text = "Account locked. Try again in 8 hours.",
                                fontSize = 14.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (otpUiState.canResend) {
                            TextButton(
                                onClick = { otpViewModel.resendOtpWithEmail(userEmail) },
                                enabled = !otpUiState.isResending
                            ) {
                                if (otpUiState.isResending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color(0xFF10B981),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = if (otpUiState.isResending) "Sending..." else "Resend Code",
                                    color = Color(0xFF10B981),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row {
                                Text(
                                    text = "Resend code in ",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "${otpUiState.timeLeft}s",
                                    fontSize = 14.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Back Button
                        OutlinedButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.DarkGray
                            ),
                            enabled = !otpUiState.isVerifying
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Verify Button
                        // Find the Verify Button in your ForgotPasswordVerifyEmailScreen.kt
// Replace the onClick handler with this:

                        Button(
                            onClick = {
                                otpViewModel.verifyOtp(
                                    email = userEmail,
                                    otpType = OtpType.FORGOT_PASSWORD
                                ) {
                                    // ✨ On success, get the token and navigate
                                    val token = otpViewModel.getVerificationToken()

                                    if (token != null) {
                                        Log.d("VerifyEmail", "Token obtained: $token")

                                        // Pass token through navigation
                                        val route = Screen.ResetPassword.createRoute(userEmail, token)
                                        navController.navigate(route) {
                                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                                        }
                                    } else {
                                        Log.e("VerifyEmail", "❌ Token is null after verification!")
                                        // Show error to user
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
                            enabled = otpUiState.otpValues.all { it.isNotEmpty() }
                                    && !otpUiState.isVerifying
                                    && !otpUiState.isLocked
                        ) {
                            if (otpUiState.isVerifying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (otpUiState.isVerifying) "Verifying..." else "Verify",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ForgotPasswordOTPInputField(
    otpValues: List<String>,
    onOtpChange: (List<String>) -> Unit,
    enabled: Boolean = true
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        otpValues.forEachIndexed { index, value ->
            ForgotPasswordOTPBox(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        val newValues = otpValues.toMutableList()
                        newValues[index] = newValue
                        onOtpChange(newValues)

                        if (newValue.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    } else if (newValue.isEmpty() && index > 0) {
                        val newValues = otpValues.toMutableList()
                        newValues[index] = ""
                        onOtpChange(newValues)
                        focusRequesters[index - 1].requestFocus()
                    }
                },
                focusRequester = focusRequesters[index],
                enabled = enabled
            )
            if (index < 5) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun ForgotPasswordOTPBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (value.isEmpty()) Color.White else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (value.isEmpty()) Color.LightGray else Color(0xFF10B981),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            enabled = enabled,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}