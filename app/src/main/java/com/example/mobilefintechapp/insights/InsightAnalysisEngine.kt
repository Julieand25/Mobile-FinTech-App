// File: insights/InsightAnalysisEngine.kt
package com.example.mobilefintechapp.insights

import com.example.mobilefintechapp.transactions.Transaction
import com.example.mobilefintechapp.transactions.TransactionStatus
import java.util.*
import kotlin.math.abs

class InsightAnalysisEngine {

    /**
     * Analyze transactions and generate spending alerts
     */
    fun generateSpendingAlerts(
        allTransactions: List<Transaction>
    ): List<SpendingAlert> {
        val alerts = mutableListOf<SpendingAlert>()
        val now = Calendar.getInstance()

        // Get current week and last week transactions
        val thisWeekTransactions = getThisWeekTransactions(allTransactions, now)
        val lastWeekTransactions = getLastWeekTransactions(allTransactions, now)

        // Analyze by category
        val categorySpending = groupByCategory(thisWeekTransactions)
        val lastWeekCategorySpending = groupByCategory(lastWeekTransactions)

        categorySpending.forEach { (category, amount) ->
            val lastWeekAmount = lastWeekCategorySpending[category] ?: 0.0
            val percentageChange = if (lastWeekAmount > 0) {
                ((amount - lastWeekAmount) / lastWeekAmount * 100).toInt()
            } else {
                100
            }

            // Determine alert type and color based on severity
            when {
                // CRITICAL: Very high increase (>50%)
                percentageChange > 50 && amount > 100 -> {
                    alerts.add(
                        SpendingAlert(
                            category = category,
                            icon = getCategoryIcon(category),
                            message = "You spent RM${amount.toInt()} on $category this week, ${abs(percentageChange)}% higher than last week!",
                            date = getCurrentDate(),
                            amount = "RM ${amount.toInt()}",
                            iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFFFE5E5), // Light Red
                            alertType = AlertType.INCREASE  // Red icon
                        )
                    )
                }
                // WARNING: Moderate increase (30-50%)
                percentageChange > 30 && amount > 100 -> {
                    alerts.add(
                        SpendingAlert(
                            category = category,
                            icon = getCategoryIcon(category),
                            message = "You spent RM${amount.toInt()} on $category this week, which is ${abs(percentageChange)}% higher than last week.",
                            date = getCurrentDate(),
                            amount = "RM ${amount.toInt()}",
                            iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFFFF9E6), // Light Yellow
                            alertType = AlertType.WARNING  // Yellow icon
                        )
                    )
                }
                // POSITIVE: Spending decreased
                percentageChange < -20 -> {
                    alerts.add(
                        SpendingAlert(
                            category = category,
                            icon = getCategoryIcon(category),
                            message = "Great job! Your $category expenses decreased by ${abs(percentageChange)}% this week. Keep it up!",
                            date = getCurrentDate(),
                            amount = "RM ${amount.toInt()}",
                            iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1), // Light Green
                            alertType = AlertType.INFO  // Green icon
                        )
                    )
                }
            }
        }

        // Check for halal compliance - CRITICAL if very low
        val halalPercentage = calculateHalalPercentage(thisWeekTransactions)
        when {
            halalPercentage < 50 -> {
                // CRITICAL: Very low halal percentage
                alerts.add(
                    SpendingAlert(
                        category = "Halal Compliance",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        message = "Only $halalPercentage% of your spending is halal-certified. Immediate action needed!",
                        date = getCurrentDate(),
                        amount = "${thisWeekTransactions.size} txns",
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFFFE5E5), // Red
                        alertType = AlertType.INCREASE
                    )
                )
            }
            halalPercentage < 70 -> {
                // WARNING: Moderate halal percentage
                alerts.add(
                    SpendingAlert(
                        category = "Halal Compliance",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        message = "Only $halalPercentage% of your spending this week is halal-certified. Consider choosing halal vendors.",
                        date = getCurrentDate(),
                        amount = "${thisWeekTransactions.size} txns",
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFFFF9E6), // Yellow
                        alertType = AlertType.WARNING
                    )
                )
            }
            halalPercentage >= 90 -> {
                // POSITIVE: High halal compliance
                alerts.add(
                    SpendingAlert(
                        category = "Halal Compliance",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        message = "Excellent! ${halalPercentage}% of your spending is halal-certified.",
                        date = getCurrentDate(),
                        amount = "${thisWeekTransactions.size} txns",
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1), // Green
                        alertType = AlertType.INFO
                    )
                )
            }
        }

        return alerts.take(5) // Return top 5 alerts
    }

    /**
     * Generate AI recommendations based on spending patterns
     * ALL RECOMMENDATIONS USE GREEN COLOR
     */
    fun generateRecommendations(
        allTransactions: List<Transaction>
    ): List<AIRecommendation> {
        val recommendations = mutableListOf<AIRecommendation>()
        val now = Calendar.getInstance()

        val thisMonthTransactions = getThisMonthTransactions(allTransactions, now)
        val categorySpending = groupByCategory(thisMonthTransactions)

        // Sort categories by spending amount
        val topSpendingCategories = categorySpending.entries
            .sortedByDescending { it.value }
            .take(3)

        // Recommendation 1: Reduce top spending category
        topSpendingCategories.firstOrNull()?.let { (category, amount) ->
            when {
                amount > 1000 -> {
                    // Very high spending
                    recommendations.add(
                        AIRecommendation(
                            title = "Reduce $category Expenses",
                            description = "You've spent RM${amount.toInt()} on $category this month. This is significantly high. Try setting a budget of RM${(amount * 0.7).toInt()} next month.",
                            category = category,
                            icon = getCategoryIcon(category),
                            iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                        )
                    )
                }
                amount > 500 -> {
                    // Moderate spending
                    recommendations.add(
                        AIRecommendation(
                            title = "Reduce $category Expenses",
                            description = "You've spent RM${amount.toInt()} on $category this month. Try setting a budget of RM${(amount * 0.8).toInt()} next month.",
                            category = category,
                            icon = getCategoryIcon(category),
                            iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                        )
                    )
                }
            }
        }

        // Recommendation 2: Halal improvement
        val halalCount = thisMonthTransactions.count { it.status == TransactionStatus.HALAL }
        val totalCount = thisMonthTransactions.size
        val halalPercentage = if (totalCount > 0) (halalCount * 100) / totalCount else 0

        when {
            halalPercentage < 50 -> {
                recommendations.add(
                    AIRecommendation(
                        title = "Improve Halal Compliance",
                        description = "Only ${halalPercentage}% of your transactions are halal. Focus on halal-certified merchants immediately.",
                        category = "Halal",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                    )
                )
            }
            halalPercentage < 80 -> {
                recommendations.add(
                    AIRecommendation(
                        title = "Improve Halal Compliance",
                        description = "Currently ${halalPercentage}% of your transactions are halal. Focus on halal-certified merchants to increase this percentage.",
                        category = "Halal",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                    )
                )
            }
            halalPercentage >= 90 -> {
                recommendations.add(
                    AIRecommendation(
                        title = "Excellent Halal Compliance",
                        description = "MashaAllah! ${halalPercentage}% of your transactions are halal. Keep up the great work!",
                        category = "Halal",
                        icon = com.example.mobilefintechapp.R.drawable.shield,
                        iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                    )
                )
            }
        }

        // Recommendation 3: Budget suggestion
        val monthlyTotal = categorySpending.values.sum()
        val averageDaily = monthlyTotal / 30

        if (monthlyTotal > 0) {
            val targetDaily = averageDaily * 0.9

            recommendations.add(
                AIRecommendation(
                    title = if (averageDaily > 100) "High Daily Spending" else "Daily Spending Target",
                    description = "Your average daily spending is RM${averageDaily.toInt()}. Try to keep it below RM${targetDaily.toInt()} to save more.",
                    category = "Budgeting",
                    icon = com.example.mobilefintechapp.R.drawable.budget,
                    iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                )
            )
        }

        // Recommendation 4: Unusual spending pattern
        val unusualCategory = detectUnusualSpending(allTransactions)
        unusualCategory?.let { (category, message, severity) ->
            recommendations.add(
                AIRecommendation(
                    title = if (severity == "high") "Unusual Spending Alert" else "Unusual Spending Detected",
                    description = message,
                    category = category,
                    icon = getCategoryIcon(category),
                    iconBackgroundColor = androidx.compose.ui.graphics.Color(0xFFE6F7F1) // Green (all recommendations)
                )
            )
        }

        return recommendations
    }

    // Helper functions
    private fun getThisWeekTransactions(transactions: List<Transaction>, now: Calendar): List<Transaction> {
        return transactions.filter { transaction ->
            val txnDate = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            txnDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    txnDate.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
        }
    }

    private fun getLastWeekTransactions(transactions: List<Transaction>, now: Calendar): List<Transaction> {
        val lastWeek = (now.clone() as Calendar).apply {
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        return transactions.filter { transaction ->
            val txnDate = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            txnDate.get(Calendar.YEAR) == lastWeek.get(Calendar.YEAR) &&
                    txnDate.get(Calendar.WEEK_OF_YEAR) == lastWeek.get(Calendar.WEEK_OF_YEAR)
        }
    }

    private fun getThisMonthTransactions(transactions: List<Transaction>, now: Calendar): List<Transaction> {
        return transactions.filter { transaction ->
            val txnDate = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            txnDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    txnDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }
    }

    private fun groupByCategory(transactions: List<Transaction>): Map<String, Double> {
        return transactions.groupBy { it.merchantCategory }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }
    }

    private fun calculateHalalPercentage(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return 100
        val halalCount = transactions.count { it.status == TransactionStatus.HALAL }
        return (halalCount * 100) / transactions.size
    }

    private fun detectUnusualSpending(transactions: List<Transaction>): Triple<String, String, String>? {
        // Detect if a category suddenly has high spending
        val now = Calendar.getInstance()
        val thisWeek = getThisWeekTransactions(transactions, now)
        val lastMonth = transactions.filter { transaction ->
            val txnDate = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            val monthAgo = (now.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            txnDate.after(monthAgo) && txnDate.before(now)
        }

        val thisWeekByCategory = groupByCategory(thisWeek)
        val lastMonthAverage = groupByCategory(lastMonth).mapValues { it.value / 4 } // Weekly average

        thisWeekByCategory.forEach { (category, amount) ->
            val average = lastMonthAverage[category] ?: 0.0
            val increaseMultiple = if (average > 0) amount / average else 0.0

            when {
                increaseMultiple > 3 && amount > 200 -> {
                    // CRITICAL: 3x normal spending
                    return Triple(
                        category,
                        "Your $category spending this week (RM${amount.toInt()}) is ${increaseMultiple.toInt()}x your average of RM${average.toInt()}/week. This is unusually high!",
                        "high"
                    )
                }
                increaseMultiple > 2 && amount > 200 -> {
                    // WARNING: 2x normal spending
                    return Triple(
                        category,
                        "Your $category spending this week (RM${amount.toInt()}) is unusually high compared to your average of RM${average.toInt()}/week.",
                        "moderate"
                    )
                }
            }
        }
        return null
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = calendar.get(Calendar.YEAR)
        return "$day/$month/$year"
    }

    /**
     * Get icon for category
     */
    private fun getCategoryIcon(category: String): Int {
        return when (category.lowercase().trim()) {
            // Food & Beverages
            "food", "restaurant", "dining", "cafe", "fast food", "food & dining", "bakery", "catering" ->
                com.example.mobilefintechapp.R.drawable.spoon_and_fork
            "beverages", "drinks", "coffee", "bubble tea", "juice", "tea", "smoothie" ->
                com.example.mobilefintechapp.R.drawable.drinks

            // Shopping
            "shopping", "retail", "grocery", "supermarket", "convenience store", "market" ->
                com.example.mobilefintechapp.R.drawable.shopping_bag

            // Clothing & Fashion
            "clothing", "fashion", "apparel", "clothes", "boutique", "shoes", "accessories" ->
                com.example.mobilefintechapp.R.drawable.tshirt

            // Beauty & Personal Care
            "beauty", "personal care", "cosmetics", "salon", "spa", "beauty & personal care",
            "skincare", "makeup", "haircare", "perfume" ->
                com.example.mobilefintechapp.R.drawable.cosmetic

            // Transportation
            "transport", "transportation", "taxi", "grab", "bus", "train", "mrt", "lrt", "commute" ->
                com.example.mobilefintechapp.R.drawable.gas_station
            "fuel", "petrol", "gas", "gas station", "shell", "petronas" ->
                com.example.mobilefintechapp.R.drawable.gas_station

            // Electronics & Technology
            "electronics", "technology", "gadgets", "computer", "phone", "laptop", "tablet",
            "smartphone", "tech", "it" ->
                com.example.mobilefintechapp.R.drawable.device

            // Entertainment
            "entertainment", "movie", "cinema", "games", "streaming", "netflix", "youtube",
            "concert", "theatre", "amusement park" ->
                com.example.mobilefintechapp.R.drawable.youtube

            // Health & Medical
            "health", "medical", "pharmacy", "clinic", "hospital", "healthcare", "doctor",
            "medicine", "dental", "optical" ->
                com.example.mobilefintechapp.R.drawable.shield

            // Bills & Utilities
            "bills", "utilities", "electricity", "water", "internet", "phone bill",
            "subscription", "insurance", "loan", "payment" ->
                com.example.mobilefintechapp.R.drawable.bill

            // Education
            "education", "books", "courses", "school", "university", "tuition", "learning",
            "training", "workshop", "seminar", "stationery" ->
                com.example.mobilefintechapp.R.drawable.open_book

            // Home & Garden
            "home", "furniture", "garden", "home improvement", "household", "appliances",
            "decor", "renovation", "hardware" ->
                com.example.mobilefintechapp.R.drawable.house

            // Sports & Fitness
            "sports", "fitness", "gym", "exercise", "workout", "yoga", "swimming",
            "athletic", "sportswear", "sports equipment" ->
                com.example.mobilefintechapp.R.drawable.sports

            // Travel
            "travel", "hotel", "flight", "vacation", "tourism", "airline", "accommodation",
            "resort", "airbnb", "booking" ->
                com.example.mobilefintechapp.R.drawable.plane

            // Financial
            "budgeting", "budget", "savings", "investment", "banking", "finance" ->
                com.example.mobilefintechapp.R.drawable.budget

            // Islamic Finance
            "halal", "halal compliance", "zakat", "sadaqah", "islamic", "mosque", "donation" ->
                com.example.mobilefintechapp.R.drawable.shield

            // Default fallback
            else -> com.example.mobilefintechapp.R.drawable.shop
        }
    }
}

// Extension function to convert Android Color to Compose Color
private fun Int.toComposeColor(): androidx.compose.ui.graphics.Color {
    return androidx.compose.ui.graphics.Color(this)
}