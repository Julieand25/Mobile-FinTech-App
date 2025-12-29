package com.example.mobilefintechapp.profile.change_email

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefintechapp.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailVerifyScreen(
    navController: NavHostController,
    userEmail: String
) {
    // Get the SAME ViewModel instance from navigation backstack
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("change_email/{password}")
    }
    val changeEmailViewModel: ChangeEmailViewModel = viewModel(parentEntry)

    val successMessage by changeEmailViewModel.successMessage.collectAsState()
    val errorMessage by changeEmailViewModel.errorMessage.collectAsState()

    var emailUpdated by remember { mutableStateOf(false) }

    // Snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-check for email update every 3 seconds
    LaunchedEffect(Unit) {
        var checkCount = 0
        while (!emailUpdated && checkCount < 60) { // Stop after 3 minutes (60 checks)
            checkCount++
            Log.d("ChangeEmailVerify", "🔍 Check #$checkCount - Checking if email is updated...")
            changeEmailViewModel.checkEmailUpdated()
            delay(3000) // Check every 3 seconds
        }

        if (checkCount >= 60) {
            Log.e("ChangeEmailVerify", "❌ Timeout: Email not verified after 3 minutes")
        }
    }

    // Navigate to profile when email is updated
    LaunchedEffect(successMessage) {
        successMessage?.let {
            Log.d("ChangeEmailVerify", "✅ Email updated! Navigating to profile...")
            emailUpdated = true
            snackbarHostState.showSnackbar(it)
            delay(2000) // Show success message for 2 seconds
            navController.navigate(Screen.Profile.route) {
                popUpTo(Screen.Profile.route) { inclusive = true }
            }
            changeEmailViewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            changeEmailViewModel.clearError()
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
                        .padding(top = 65.dp),
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

                    // Title Section
                    Column {
                        Text(
                            text = if (emailUpdated) "Success!" else "Verify Email",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = if (emailUpdated) "Email changed" else "Check your inbox",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // Verification Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-65).dp)
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
                        // Email Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .offset(y = (-20).dp)
                                .background(
                                    color = if (emailUpdated)
                                        Color(0xFF10B981).copy(alpha = 0.15f)
                                    else
                                        Color(0xFF2196F3).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (emailUpdated)
                                    Icons.Default.CheckCircle
                                else
                                    Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = if (emailUpdated)
                                    Color(0xFF10B981)
                                else
                                    Color(0xFF2196F3),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(
                            text = if (emailUpdated)
                                "Email Changed!"
                            else
                                "Check Your Email",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message
                        if (!emailUpdated) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color.DarkGray)) {
                                        append("We've sent a verification email to ")
                                    }
                                    withStyle(style = SpanStyle(
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.SemiBold
                                    )) {
                                        append(userEmail)
                                    }
                                    withStyle(style = SpanStyle(color = Color.DarkGray)) {
                                        append(". Please check your inbox and click the verification link.")
                                    }
                                },
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Instructions Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Next Steps:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "1. Open your email inbox\n2. Find the verification email from Firebase\n3. Click the verification link\n4. Your email will be updated automatically",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Checking indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF10B881),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Waiting for verification...",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Cancel Button
                            TextButton(
                                onClick = {
                                    navController.navigate(Screen.Profile.route) {
                                        popUpTo(Screen.Profile.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Cancel",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // Success message
                            Text(
                                text = "Your email has been successfully changed to $userEmail!",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Redirecting to profile...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}