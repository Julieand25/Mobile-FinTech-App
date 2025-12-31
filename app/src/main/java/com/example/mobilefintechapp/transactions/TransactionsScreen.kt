package com.example.mobilefintechapp.transactions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilefintechapp.R
import com.example.mobilefintechapp.components.BottomNavigationBar
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(navController: NavHostController) {
    val viewModel: TransactionViewModel = viewModel()

    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.TODAY) }
    var selectedCategoryFilter by remember { mutableStateOf(CategoryFilter.ALL) }

    // Observe all transactions
    val allTransactions by viewModel.allTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Filter by TIME ONLY first (don't apply category filter yet for counting)
    val timeFilteredTransactions = remember(allTransactions, selectedTimeFilter) {
        viewModel.getFilteredTransactions(selectedTimeFilter, CategoryFilter.ALL)  // ← Get ALL categories for selected time
    }

    // Calculate statistics from TIME-FILTERED transactions (not category-filtered)
    val (halalCount, unknownCount, haramCount) = remember(timeFilteredTransactions) {
        viewModel.getTransactionCounts(timeFilteredTransactions)  // ← Count from time-filtered only
    }

    val totalCount = timeFilteredTransactions.size

    val totalAmount = remember(timeFilteredTransactions) {
        viewModel.calculateTotalAmount(timeFilteredTransactions)
    }

    // NOW apply BOTH time and category filters for display
    val filteredTransactions = remember(allTransactions, selectedTimeFilter, selectedCategoryFilter) {
        viewModel.getFilteredTransactions(selectedTimeFilter, selectedCategoryFilter)
    }

    val halalPercentage = if (totalCount > 0) (halalCount.toFloat() / totalCount * 100).toInt() else 0
    val unknownPercentage = if (totalCount > 0) (unknownCount.toFloat() / totalCount * 100).toInt() else 0
    val haramPercentage = if (totalCount > 0) (haramCount.toFloat() / totalCount * 100).toInt() else 0

    Box(modifier = Modifier.fillMaxSize()) {
        // Main scrollable content
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
                    .height(510.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF10B881),
                                Color(0xFF0E9788)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 50.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
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

                    // Time Filter Chips
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

                    // Statistics Box
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF3C0000).copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(vertical = 16.dp)
                        ) {
                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(80.dp),
                                        color = Color.White
                                    )
                                }
                            } else if (allTransactions.isEmpty()) {
                                // Empty state
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.shop),
                                        contentDescription = "No transactions",
                                        tint = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No transactions yet",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Link a bank account to view transactions",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
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
                }
            }

            // Category Filter Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
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
                            label = "      All      ",
                            isSelected = selectedCategoryFilter == CategoryFilter.ALL,
                            onClick = { selectedCategoryFilter = CategoryFilter.ALL },
                        )
                        CategoryFilterItem(
                            count = halalCount,
                            label = "    Halal    ",
                            isSelected = selectedCategoryFilter == CategoryFilter.HALAL,
                            onClick = { selectedCategoryFilter = CategoryFilter.HALAL },
                        )
                        CategoryFilterItem(
                            count = unknownCount,
                            label = " Unknown ",
                            isSelected = selectedCategoryFilter == CategoryFilter.UNKNOWN,
                            onClick = { selectedCategoryFilter = CategoryFilter.UNKNOWN },
                        )
                        CategoryFilterItem(
                            count = haramCount,
                            label = "   Haram   ",
                            isSelected = selectedCategoryFilter == CategoryFilter.HARAM,
                            onClick = { selectedCategoryFilter = CategoryFilter.HARAM },
                        )
                    }
                }
            }

            // Transaction List Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp)
            ) {
                Text(
                    text = "Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (filteredTransactions.isEmpty() && allTransactions.isNotEmpty()) {
                    // No transactions for selected filter
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions for selected filter",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Transaction cards
                    filteredTransactions.forEach { transaction ->
                        TransactionCard(transaction = transaction)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Bottom spacing for navigation bar
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
            val canvasWidth = size.width
            val canvasHeight = size.height
            val strokeWidth = 30.dp.toPx()

            val radius = (minOf(canvasWidth, canvasHeight) / 2) - strokeWidth / 2
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2

            // Draw segments with drawCircle approach for each segment
            val total = halalPercentage + unknownPercentage + haramPercentage
            if (total > 0) {
                var startAngle = -90f

                // Halal
                if (halalPercentage > 0) {
                    val sweep = (halalPercentage.toFloat() / 100f) * 360f
                    drawArc(
                        color = Color(0xFF10B881),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Square  // Try Square cap
                        ),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(centerX - radius, centerY - radius)
                    )
                    startAngle += sweep
                }

                // Unknown
                if (unknownPercentage > 0) {
                    val sweep = (unknownPercentage.toFloat() / 100f) * 360f
                    drawArc(
                        color = Color(0xFFFFA726),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Square
                        ),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(centerX - radius, centerY - radius)
                    )
                    startAngle += sweep
                }

                // Haram
                if (haramPercentage > 0) {
                    val sweep = (haramPercentage.toFloat() / 100f) * 360f
                    drawArc(
                        color = Color(0xFFEF5350),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Square
                        ),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(centerX - radius, centerY - radius)
                    )
                }
            }
        }

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
            .padding(6.dp),
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
            // Category Icon with Gray Background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), CircleShape), // Gray background for all
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getCategoryIcon(transaction.merchantCategory)),
                    contentDescription = transaction.merchantCategory,
                    tint = Color(0xFF6B7280), // Gray icon color
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchantName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = transaction.merchantCategory,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = transaction.getFormattedDate(),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

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
                        text = transaction.status.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
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

/**
 * Get icon for category (same logic as InsightAnalysisEngine)
 */
private fun getCategoryIcon(category: String): Int {
    return when (category.lowercase().trim()) {
        // Food & Beverages
        "food", "restaurant", "dining", "cafe", "fast food", "food & dining", "bakery", "catering", "groceries" ->
            R.drawable.spoon_and_fork
        "beverages", "drinks", "coffee", "bubble tea", "juice", "tea", "smoothie" ->
            R.drawable.drinks

        // Shopping
        "shopping", "retail", "grocery", "supermarket", "convenience store", "market", "convenience", "variety store" ->
            R.drawable.shopping_bag

        // Clothing & Fashion
        "clothing", "fashion", "apparel", "clothes", "boutique", "shoes", "accessories" ->
            R.drawable.tshirt

        // Beauty & Personal Care
        "beauty", "personal care", "cosmetics", "salon", "spa", "beauty & personal care",
        "skincare", "makeup", "haircare", "perfume" ->
            R.drawable.cosmetic

        // Transportation
        "transport", "transportation", "taxi", "grab", "bus", "train", "mrt", "lrt", "commute" ->
            R.drawable.gas_station
        "fuel", "petrol", "gas", "gas station", "shell", "petronas" ->
            R.drawable.gas_station

        // Electronics & Technology
        "electronics", "technology", "gadgets", "computer", "phone", "laptop", "tablet",
        "smartphone", "tech", "it" ->
            R.drawable.device

        // Entertainment
        "entertainment", "movie", "cinema", "games", "streaming", "netflix", "youtube",
        "concert", "theatre", "amusement park" ->
            R.drawable.youtube

        // Health & Medical
        "health", "medical", "pharmacy", "clinic", "hospital", "healthcare", "doctor",
        "medicine", "dental", "optical" ->
            R.drawable.shield

        // Bills & Utilities
        "bills", "utilities", "electricity", "water", "internet", "phone bill",
        "subscription", "insurance", "loan", "payment", "telecom" ->
            R.drawable.bill

        // Education
        "education", "books", "courses", "school", "university", "tuition", "learning",
        "training", "workshop", "seminar", "stationery" ->
            R.drawable.open_book

        // Home & Garden
        "home", "furniture", "garden", "home improvement", "household", "appliances",
        "decor", "renovation", "hardware" ->
            R.drawable.house

        // Sports & Fitness
        "sports", "fitness", "gym", "exercise", "workout", "yoga", "swimming",
        "athletic", "sportswear", "sports equipment", "sports & recreation" ->
            R.drawable.sports

        // Travel
        "travel", "hotel", "flight", "vacation", "tourism", "airline", "accommodation",
        "resort", "airbnb", "booking" ->
            R.drawable.plane

        // Financial
        "budgeting", "budget", "savings", "investment", "banking", "finance" ->
            R.drawable.budget

        // Islamic Finance
        "halal", "halal compliance", "zakat", "sadaqah", "islamic", "mosque", "donation" ->
            R.drawable.shield

        // Alcohol & Gambling (keep as shop or use warning icon)
        "liquor store", "tobacco", "gambling" ->
            R.drawable.shop

        // Default fallback
        else -> R.drawable.shop
    }
}