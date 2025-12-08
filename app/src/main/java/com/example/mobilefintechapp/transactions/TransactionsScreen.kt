package com.example.mobilefintechapp.transactions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.components.BottomNavigationBar
import com.example.mobilefintechapp.homepage.HalalFinanceTheme
import kotlin.math.cos
import kotlin.math.sin

// Data classes
data class Transaction(
    val name: String,
    val category: String,
    val amount: Double,
    val date: String,
    val status: TransactionStatus
)

enum class TransactionStatus {
    HALAL, UNKNOWN, HARAM
}

enum class TimeFilter {
    TODAY, THIS_WEEK, THIS_MONTH
}

enum class CategoryFilter {
    ALL, HALAL, UNKNOWN, HARAM
}

@Composable
fun TransactionsScreen(navController: NavHostController) {
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.TODAY) }
    var selectedCategoryFilter by remember { mutableStateOf(CategoryFilter.ALL) }

    // Sample transaction data
    val allTransactions = listOf(
        Transaction("Mydin", "Groceries", 50.0, "07/11/2025 - 14:30", TransactionStatus.HALAL),
        Transaction("KFC", "Food", 25.0, "06/11/2025 - 19:45", TransactionStatus.UNKNOWN),
        Transaction("7-Eleven", "Convenience", 10.0, "05/11/2025 - 08:15", TransactionStatus.HALAL),
        Transaction("Shell", "Fuel", 80.0, "04/11/2025 - 16:20", TransactionStatus.HALAL),
        Transaction("McDonald's", "Food", 18.0, "03/11/2025 - 12:30", TransactionStatus.HARAM),
        Transaction("Tesco", "Groceries", 120.0, "02/11/2025 - 10:45", TransactionStatus.HALAL),
        Transaction("Starbucks", "Beverages", 15.0, "01/11/2025 - 09:00", TransactionStatus.UNKNOWN)
    )

    // Filter transactions based on selected filters
    val filteredTransactions = allTransactions.filter { transaction ->
        when (selectedCategoryFilter) {
            CategoryFilter.ALL -> true
            CategoryFilter.HALAL -> transaction.status == TransactionStatus.HALAL
            CategoryFilter.UNKNOWN -> transaction.status == TransactionStatus.UNKNOWN
            CategoryFilter.HARAM -> transaction.status == TransactionStatus.HARAM
        }
    }

    // Calculate totals and percentages
    val totalAmount = when (selectedTimeFilter) {
        TimeFilter.TODAY -> 10.0
        TimeFilter.THIS_WEEK -> 165.0
        TimeFilter.THIS_MONTH -> 350.0
    }

    val halalCount = filteredTransactions.count { it.status == TransactionStatus.HALAL }
    val unknownCount = filteredTransactions.count { it.status == TransactionStatus.UNKNOWN }
    val haramCount = filteredTransactions.count { it.status == TransactionStatus.HARAM }
    val totalCount = filteredTransactions.size

    val halalPercentage = if (totalCount > 0) (halalCount.toFloat() / totalCount * 100).toInt() else 0
    val unknownPercentage = if (totalCount > 0) (unknownCount.toFloat() / totalCount * 100).toInt() else 0
    val haramPercentage = if (totalCount > 0) (haramCount.toFloat() / totalCount * 100).toInt() else 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            // Green Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(490.dp)
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
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 50.dp)
                ) {
                    // Header with title and calendar icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transactions",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { /* TODO: Open calendar */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.calendar_icon),
                                contentDescription = "Calendar",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(
                            "Today" to TimeFilter.TODAY,
                            "This Week" to TimeFilter.THIS_WEEK,
                            "This Month" to TimeFilter.THIS_MONTH
                        ).forEach { (label, filter) ->

                            FilterChip(
                                selected = selectedTimeFilter == filter,
                                onClick = { selectedTimeFilter = filter },
                                label = { Text(label) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Background box (curved, 6% opacity)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(262.dp)
                                .background(
                                    color = Color(0xFF3C0000).copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        )

                        // Foreground content (DonutChart + Legend)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // Donut Chart
                            DonutChart(
                                halalPercentage = halalPercentage,
                                unknownPercentage = unknownPercentage,
                                haramPercentage = haramPercentage,
                                totalAmount = totalAmount
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Legend
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LegendItem("${halalPercentage}%", "Halal", Color(0xFF10B881))
                                LegendItem("${unknownPercentage}%", "Unknown", Color(0xFFFFA726))
                                LegendItem("${haramPercentage}%", "Haram", Color(0xFFEF5350))
                            }
                        }
                    }
                }
            }

            // Category Filter Card (Overlapping)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CategoryFilterItem(
                            count = totalCount,
                            label = "All",
                            isSelected = selectedCategoryFilter == CategoryFilter.ALL,
                            onClick = { selectedCategoryFilter = CategoryFilter.ALL },
                        )
                        CategoryFilterItem(
                            count = halalCount,
                            label = "Halal",
                            isSelected = selectedCategoryFilter == CategoryFilter.HALAL,
                            onClick = { selectedCategoryFilter = CategoryFilter.HALAL },
                        )
                        CategoryFilterItem(
                            count = unknownCount,
                            label = "Unknown",
                            isSelected = selectedCategoryFilter == CategoryFilter.UNKNOWN,
                            onClick = { selectedCategoryFilter = CategoryFilter.UNKNOWN },
                        )
                        CategoryFilterItem(
                            count = haramCount,
                            label = "Haram",
                            isSelected = selectedCategoryFilter == CategoryFilter.HARAM,
                            onClick = { selectedCategoryFilter = CategoryFilter.HARAM },
                        )
                    }
                }
            }

            // Transaction List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp)
            ) {
                Text(
                    text = "Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                filteredTransactions.forEach { transaction ->
                    TransactionCard(transaction = transaction)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun TimeFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF10B881) else Color.White
        )
    }
}

@Composable
fun DonutChart(
    halalPercentage: Int,
    unknownPercentage: Int,
    haramPercentage: Int,
    totalAmount: Double
) {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 30.dp.toPx()
            val size = this.size
            val radius = (size.minDimension - strokeWidth) / 2
            val topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )

            // Calculate angles
            val halalAngle = (halalPercentage / 100f) * 360f
            val unknownAngle = (unknownPercentage / 100f) * 360f
            val haramAngle = (haramPercentage / 100f) * 360f

            // Draw Halal segment (starts at -90 degrees, top of circle)
            if (halalPercentage > 0) {
                drawArc(
                    color = Color(0xFF10B881),
                    startAngle = -90f,
                    sweepAngle = halalAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Draw Unknown segment
            if (unknownPercentage > 0) {
                drawArc(
                    color = Color(0xFFFFA726),
                    startAngle = -90f + halalAngle,
                    sweepAngle = unknownAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Draw Haram segment
            if (haramPercentage > 0) {
                drawArc(
                    color = Color(0xFFEF5350),
                    startAngle = -90f + halalAngle + unknownAngle,
                    sweepAngle = haramAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "RM ${totalAmount.toInt()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun LegendItem(
    percentage: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = percentage,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun CategoryFilterItem(
    count: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFFF5F5F5) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shop Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shop),
                    contentDescription = "Shop",
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = transaction.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = transaction.date,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Amount and Status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-RM ${transaction.amount.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = when (transaction.status) {
                                TransactionStatus.HALAL -> Color(0xFF10B881).copy(alpha = 0.15f)
                                TransactionStatus.UNKNOWN -> Color(0xFFFFA726).copy(alpha = 0.15f)
                                TransactionStatus.HARAM -> Color(0xFFEF5350).copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (transaction.status) {
                            TransactionStatus.HALAL -> "Halal"
                            TransactionStatus.UNKNOWN -> "Unknown"
                            TransactionStatus.HARAM -> "Haram"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (transaction.status) {
                            TransactionStatus.HALAL -> Color(0xFF10B881)
                            TransactionStatus.UNKNOWN -> Color(0xFFFFA726)
                            TransactionStatus.HARAM -> Color(0xFFEF5350)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
    HalalFinanceTheme {
        TransactionsScreen(navController = rememberNavController())
    }
}