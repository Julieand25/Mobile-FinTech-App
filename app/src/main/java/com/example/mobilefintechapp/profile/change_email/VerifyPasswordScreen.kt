package com.example.mobilefintechapp.profile.change_email

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.navigation.Screen
import com.example.mobilefintechapp.profile.change_email.VerifyPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPasswordScreen(navController: NavHostController) {
    val viewModel: VerifyPasswordViewModel = viewModel()

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isPasswordVerified by viewModel.isPasswordVerified.collectAsState()

    // Navigate to ChangeEmailScreen when password is verified
    LaunchedEffect(isPasswordVerified) {
        if (isPasswordVerified) {
            navController.navigate(Screen.ChangeEmail.route) {
                popUpTo(Screen.VerifyPasswordForEmail.route) { inclusive = true }
            }
            viewModel.resetVerification()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Green Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF10B881),
                                Color(0xFF0E9788)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 6.dp)
                        .padding(top = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = {
                            navController.popBackStack()
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

                    Column {
                        // Title
                        Text(
                            text = "Verify Password",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Enter your password to continue",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // Card with Lock Icon overlapping
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-35).dp)
            ) {
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
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Lock Icon overlapping at top
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .offset(y = (-25).dp)
                                .background(
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.lock_green),
                                contentDescription = "Lock Icon",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Description Text
                        Text(
                            text = "For security purposes, please verify your identity by entering your current password",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Password Field Label
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Current Password",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    viewModel.clearError()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Enter your password",
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        fontSize = 14.sp
                                    )
                                },
                                trailingIcon = {
                                    if (password.isNotEmpty()) {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                painter = painterResource(
                                                    id = if (passwordVisible)
                                                        R.drawable.ic_eye_open
                                                    else
                                                        R.drawable.ic_eye_closed
                                                ),
                                                contentDescription = if (passwordVisible)
                                                    "Hide password"
                                                else
                                                    "Show password",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                },
                                visualTransformation = if (passwordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (errorMessage != null) Color.Red else Color(0xFF10B981),
                                    unfocusedBorderColor = if (errorMessage != null) Color.Red else Color.LightGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFF5F5F5)
                                ),
                                isError = errorMessage != null
                            )

                            // Error message
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Forgot Password Link
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Forgot Password?",
                                color = Color(0xFF10B981),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(Screen.InsertEmail.route)
                                    }
                                    .padding(vertical = 4.dp)
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
                                    navController.popBackStack()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.DarkGray
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(Color.LightGray, Color.LightGray))
                                ),
                                enabled = !isLoading
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Continue Button
                            Button(
                                onClick = {
                                    viewModel.verifyPassword(password)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                enabled = password.isNotEmpty() && !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Continue",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    // Add navigation with password
    LaunchedEffect(isPasswordVerified) {
        if (isPasswordVerified) {
            navController.navigate("change_email/$password") {
                popUpTo(Screen.VerifyPasswordForEmail.route) { inclusive = true }
            }
            viewModel.resetVerification()
        }
    }
}

@Composable
fun HalalFinanceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF10B981),
            secondary = Color(0xFF0E9788),
            background = Color(0xFF10B881)
        ),
        content = content
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyPasswordScreenPreview() {
    HalalFinanceTheme {
        //VerifyPasswordScreen()
    }
}