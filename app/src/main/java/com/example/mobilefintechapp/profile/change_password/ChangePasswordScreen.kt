package com.example.mobilefintechapp.profile.change_password

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefintechapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Handle success - navigate back
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
        }
    }

    // Loading Dialog
    if (uiState.isLoading) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Updating Password") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF10B981)
                    )
                    Text("Please wait...")
                }
            },
            confirmButton = { }
        )
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
                    .height(180.dp)
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
                        .padding(top = 60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Back Button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Title Section
                    Column {
                        Text(
                            text = "Change Password",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Update your account password",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // White Card
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
                    ) {
                        // Current Password
                        Text(
                            text = "Current Password",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                viewModel.clearErrors()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Enter your current password",
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                if (currentPassword.isNotEmpty()) {
                                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (currentPasswordVisible)
                                                    R.drawable.ic_eye_open
                                                else
                                                    R.drawable.ic_eye_closed
                                            ),
                                            contentDescription = if (currentPasswordVisible)
                                                "Hide password"
                                            else
                                                "Show password",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            },
                            visualTransformation = if (currentPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (uiState.currentPasswordError != null)
                                    Color.Red else Color(0xFF10B981),
                                unfocusedBorderColor = if (uiState.currentPasswordError != null)
                                    Color.Red else Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            ),
                            isError = uiState.currentPasswordError != null
                        )

                        // Current Password Error
                        if (uiState.currentPasswordError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.currentPasswordError ?: "",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // New Password
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
                                viewModel.clearErrors()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Enter your new password",
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            },
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
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm New Password
                        Text(
                            text = "Confirm New Password",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                viewModel.clearErrors()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Confirm your new password",
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            },
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
                                focusedBorderColor = if (uiState.confirmPasswordError != null)
                                    Color.Red else Color(0xFF10B981),
                                unfocusedBorderColor = if (uiState.confirmPasswordError != null)
                                    Color.Red else Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            ),
                            isError = uiState.confirmPasswordError != null
                        )

                        // Confirm Password Error
                        if (uiState.confirmPasswordError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.confirmPasswordError ?: "",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // General Error
                        if (uiState.generalError != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.generalError ?: "",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
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
                                onClick = { navController.popBackStack() },
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
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Confirm Button
                            Button(
                                onClick = {
                                    viewModel.validateAndChangePassword(
                                        currentPassword = currentPassword,
                                        newPassword = newPassword,
                                        confirmPassword = confirmPassword
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                enabled = currentPassword.isNotEmpty() &&
                                        newPassword.isNotEmpty() &&
                                        confirmPassword.isNotEmpty() &&
                                        !uiState.isLoading
                            ) {
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
        }
    }
}