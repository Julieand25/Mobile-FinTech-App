package com.example.mobilefintechapp.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.components.BottomNavigationBar
import com.example.mobilefintechapp.homepage.HalalFinanceTheme

@Composable
fun ProfileScreen(navController: NavHostController) {
    var spendingAlerts by remember { mutableStateOf(true) }
    var goalReminders by remember { mutableStateOf(true) }
    var darkTheme by remember { mutableStateOf(false) }
    var hideTransactionAmounts by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBankSelectionDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Green Header Section
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    // User Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "User Profile",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            //.align(Alignment.Center)
                            .fillMaxWidth(0.7f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Spacer(modifier = Modifier.height(12.dp))

                        // User Name
                        Text(
                            text = "Ahmad Rahman",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email
                        Text(
                            text = "ahmad.rahman@email.com",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
                    .offset(y = (-45).dp),
            ) {
                // Account Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Address Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO: Navigate to email settings */ }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE3F2FD), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.email),
                                    contentDescription = "Email",
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Email Address",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "ahmad.rahman@email.com",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                        // Change Password Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO: Navigate to change password */ }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFFFF3E0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock),
                                    contentDescription = "Password",
                                    tint = Color(0xFFFFA726),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Change Password",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Navigate",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bank Accounts Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bank Accounts",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "+ Add",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B881),
                                modifier = Modifier.clickable { showBankSelectionDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bank Account Item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF00ACC1), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "BSN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "BSN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "****5678 • Current",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = "Remove",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF5350),
                                modifier = Modifier.clickable { /* TODO: Remove bank account */ }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notifications Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Notifications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Spending Alerts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Spending Alerts",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Get notified about unusual spending",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = spendingAlerts,
                                onCheckedChange = { spendingAlerts = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B881),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Goal Reminders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Goal Reminders",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Reminders for savings goals",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = goalReminders,
                                onCheckedChange = { goalReminders = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B881),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "App Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dark Theme
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dark Theme",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Switch to dark mode",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = darkTheme,
                                onCheckedChange = { darkTheme = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B881),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hide Transaction Amounts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hide Transaction Amounts",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Hide amounts in notifications",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = hideTransactionAmounts,
                                onCheckedChange = { hideTransactionAmounts = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B881),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    // TODO: Implement logout functionality
                    showLogoutDialog = false
                }
            )
        }

        if (showBankSelectionDialog) {
            BankSelectionDialog(
                onDismiss = { showBankSelectionDialog = false },
                onBankSelected = { bankName ->
                    // TODO: Handle bank selection
                    showBankSelectionDialog = false
                }
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon Circle Background
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color(0xFFFFE5E5),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.log_out_red),
                            contentDescription = "Logout Icon",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Logout Confirmation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = "Are you sure you want to logout from your account?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE0E0E0)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Logout Button
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            )
                        ) {
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BankSelectionDialog(
    onDismiss: () -> Unit,
    onBankSelected: (String) -> Unit
) {
    var selectedBank by remember { mutableStateOf<String?>(null) }

    val banks = listOf(
        BankItem("Maybank", "Malayan Banking Berhad", R.drawable.maybank_logo),
        BankItem("RHB Bank", "RHB Bank Berhad", R.drawable.rhb_logo),
        BankItem("BSN", "Bank Simpanan Nasional", R.drawable.bsn_logo),
        BankItem("Bank Rakyat", "Bank Rakyat Malaysia Berhad", R.drawable.bankrakyat_logo),
        BankItem("CIMB Bank", "CIMB Bank Berhad", R.drawable.cimb_logo),
        BankItem("Public Bank", "Public Bank Berhad", R.drawable.publicbank_logo),
        BankItem("Hong Leong Bank", "Hong Leong Bank Berhad", R.drawable.hongleongbank_logo),
        BankItem("AmBank", "AmBank (M) Berhad", R.drawable.ambank_logo),
        BankItem("Affin Bank", "Affin Bank Berhad", R.drawable.affinbank_logo),
        BankItem("Alliance Bank", "Alliance Bank Malaysia Berhad", R.drawable.alliance_logo)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Title
                    Text(
                        text = "Select Your Bank",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Scrollable Bank List
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        banks.forEach { bank ->
                            BankListItem(
                                bankName = bank.name,
                                bankFullName = bank.fullName,
                                logoRes = bank.logoRes,
                                isSelected = selectedBank == bank.name,
                                onClick = { selectedBank = bank.name }
                            )

                            if (bank != banks.last()) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE0E0E0)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
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
                            onClick = {
                                selectedBank?.let { onBankSelected(it) }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B881),
                                disabledContainerColor = Color(0xFF10B881).copy(alpha = 0.5f)
                            ),
                            enabled = selectedBank != null
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BankListItem(
    bankName: String,
    bankFullName: String,
    logoRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF10B881) else Color(0xFFE0E0E0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bank Logo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = bankName,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Bank Name and Full Name
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bankName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = bankFullName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Checkmark icon when selected
            /*if (isSelected) {
                Icon(
                    painter = painterResource(id = android.R.drawable.checkbox_on_background),
                    contentDescription = "Selected",
                    tint = Color(0xFF10B881),
                    modifier = Modifier.size(24.dp)
                )
            }*/
        }
    }
}

// Data class for bank information
data class BankItem(
    val name: String,
    val fullName: String,
    val logoRes: Int
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    HalalFinanceTheme {
        ProfileScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun LogoutDialogPreview() {
    HalalFinanceTheme {
        LogoutConfirmationDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}

// Add preview for the dialog
@Preview(showBackground = true)
@Composable
fun BankSelectionDialogPreview() {
    HalalFinanceTheme {
        BankSelectionDialog(
            onDismiss = {},
            onBankSelected = {}
        )
    }
}
