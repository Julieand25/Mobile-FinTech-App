package com.example.mobilefintechapp.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpVerifyEmailScreen(userEmail: String = "ahmad@gmail.com") {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var timeLeft by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Countdown timer
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            canResend = true
        }
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
                // Back Button with Circle Background
                IconButton(
                    onClick = { /* TODO: Navigate back */ },
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

                // Title and Subtitle - Centered
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
                        text = "Enter the 6-digit code sent to the email",
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

                    // Email Icon with Circle Background
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
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Enter the 6-digit code sent to ",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP Input Boxes
                    OTPInputField(
                        otpValues = otpValues,
                        onOtpChange = { newValues ->
                            otpValues = newValues
                        }
                    )

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

                        if (canResend) {
                            TextButton(
                                onClick = {
                                    // TODO: Resend OTP
                                    timeLeft = 60
                                    canResend = false
                                    otpValues = List(6) { "" }
                                }
                            ) {
                                Text(
                                    text = "Resend Code",
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
                                    text = "${timeLeft}s",
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
                            onClick = { /* TODO: Navigate back */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.DarkGray
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Color.LightGray, Color.LightGray))
                            )
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Verify Button
                        Button(
                            onClick = { /* TODO: Verify OTP */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            ),
                            enabled = otpValues.all { it.isNotEmpty() }
                        ) {
                            Text(
                                text = "Verify",
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
fun OTPInputField(
    otpValues: List<String>,
    onOtpChange: (List<String>) -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        otpValues.forEachIndexed { index, value ->
            OTPBox(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        val newValues = otpValues.toMutableList()
                        newValues[index] = newValue
                        onOtpChange(newValues)

                        // Auto focus next box
                        if (newValue.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    } else if (newValue.isEmpty() && index > 0) {
                        // Focus previous box on delete
                        focusRequesters[index - 1].requestFocus()
                    }
                },
                focusRequester = focusRequesters[index]
            )
            if (index < 5) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun OTPBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpVerifyEmailScreenPreview() {
    HalalFinanceTheme {
        SignUpVerifyEmailScreen()
    }
}