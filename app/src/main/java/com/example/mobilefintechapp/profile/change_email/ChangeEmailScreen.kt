package com.example.mobilefintechapp.profile.change_email

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(currentEmail: String = "ahmad.rahman@email.com") {
    var newEmail by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }

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
                        .padding(top = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Back Button
                    IconButton(
                        onClick = { /* TODO: Navigate back */ },
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
                            text = "Change Email",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Enter your new email address",
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
                        // Current Email (Read-only)
                        Text(
                            text = "Current Email",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = currentEmail,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color.LightGray,
                                disabledContainerColor = Color(0xFFF5F5F5),
                                disabledTextColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // New Email Address
                        Text(
                            text = "New Email Address",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Enter your new email",
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                if (newEmail.isNotEmpty()) {
                                    IconButton(onClick = { newEmail = "" }) {
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
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm New Email
                        Text(
                            text = "Confirm New Email",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = confirmEmail,
                            onValueChange = { confirmEmail = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Confirm your new email",
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                if (confirmEmail.isNotEmpty()) {
                                    IconButton(onClick = { confirmEmail = "" }) {
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
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cancel Button
                            OutlinedButton(
                                onClick = { /* TODO: Navigate back or cancel */ },
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

                            // Continue Button
                            Button(
                                onClick = { /* TODO: Change email */ },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                enabled = newEmail.isNotEmpty() &&
                                        confirmEmail.isNotEmpty() &&
                                        newEmail == confirmEmail
                            ) {
                                Text(
                                    text = "Continue",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangeEmailScreenPreview() {
    HalalFinanceTheme {
        ChangeEmailScreen()
    }
}