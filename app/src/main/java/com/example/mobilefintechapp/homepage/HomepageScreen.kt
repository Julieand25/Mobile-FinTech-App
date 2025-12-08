package com.example.mobilefintechapp.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf("Day") }
    var showNotifications by remember { mutableStateOf(false) }

    // Sample data based on selected tab
    val totalSpent = when (selectedTab) {
        "Day" -> "RM 10"
        "Week" -> "RM 150"
        "Month" -> "RM 500"
        else -> "RM 10"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Evening",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ahmad Rahman",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Notification Icon with Badge
                Box {
                    IconButton(
                        onClick = { showNotifications = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Notification Badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Day/Week/Month Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Day", "Week", "Month").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            selectedLabelColor = Color(0xFF10B881),
                            containerColor = Color.White.copy(alpha = 0.3f),
                            labelColor = Color.White
                        ),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Total Spent Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Spent This $selectedTab",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalSpent,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Category Cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .offset(y = (-25).dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    CategoryCard(
                        title = "Halal",
                        percentage = "67%",
                        color = Color(0xFF10B881),
                        icon = Icons.Default.Check,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        title = "Unknown",
                        percentage = "28%",
                        color = Color(0xFFFFA726),
                        icon = Icons.Default.Info,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        title = "Haram",
                        percentage = "5%",
                        color = Color(0xFFEF5350),
                        icon = Icons.Default.Close,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Transactions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextButton(onClick = { /* TODO: Navigate to all transactions */ }) {
                        Text(
                            text = "View All",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transaction Items
                TransactionItem(
                    name = "Mydin",
                    category = "Groceries",
                    amount = "-RM 50",
                    status = "Halal",
                    statusColor = Color(0xFF10B881)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TransactionItem(
                    name = "KFC",
                    category = "Food",
                    amount = "-RM 25",
                    status = "Unknown",
                    statusColor = Color(0xFFFFA726)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TransactionItem(
                    name = "7-Eleven",
                    category = "Convenience",
                    amount = "-RM 10",
                    status = "Halal",
                    statusColor = Color(0xFF10B881)
                )
            }

            // Savings Goals Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Savings Goals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextButton(onClick = { /* TODO: Navigate to manage goals */ }) {
                        Text(
                            text = "Manage",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Items
                GoalItem(
                    name = "Umrah",
                    currentAmount = "RM 500",
                    targetAmount = "RM 3,000",
                    percentage = 17
                )

                Spacer(modifier = Modifier.height(20.dp))

                GoalItem(
                    name = "Emergency Fund",
                    currentAmount = "RM 1,200",
                    targetAmount = "RM 5,000",
                    percentage = 24
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation - NOW USING NAVCONTROLLER
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Notification Dialog
        if (showNotifications) {
            NotificationDialog(onDismiss = { showNotifications = false })
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    percentage: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = percentage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun TransactionItem(
    name: String,
    category: String,
    amount: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shop),
                    contentDescription = "Shop Icon",
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun GoalItem(
    name: String,
    currentAmount: String,
    targetAmount: String,
    percentage: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "$percentage%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF10B881),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentAmount,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = targetAmount,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NotificationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notification Items
                NotificationItem(
                    title = "Monthly Budget Alert",
                    message = "You have exceeded your monthly budget by RM200.",
                    date = "01/11/2025 - 09:30",
                    iconColor = Color.Red,
                    isRead = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                NotificationItem(
                    title = "Goal Achievement",
                    message = "Congratulations! You reached 80% of your Zakat goal.",
                    date = "30/10/2025 - 14:15",
                    iconColor = Color(0xFF10B881),
                    isRead = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // View All Button
                Button(
                    onClick = { /* TODO: Navigate to all notifications */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    )
                ) {
                    Text(
                        text = "View All Notifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    message: String,
    date: String,
    iconColor: Color,
    isRead: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isRead) Color.Transparent else Color(0xFFF5F5F5),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (iconColor == Color.Red) Icons.Default.Info else Icons.Default.Check,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (!isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF10B881), CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                fontSize = 11.sp,
                color = Color.Gray
            )
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
fun HomepageScreenPreview() {
    HalalFinanceTheme {
        HomepageScreen(navController = rememberNavController())
    }
}