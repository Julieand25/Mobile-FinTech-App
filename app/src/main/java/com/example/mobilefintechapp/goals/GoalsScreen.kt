package com.example.mobilefintechapp.goals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.*

@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var showContributionDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var showDeleteGoalDialog by remember { mutableStateOf(false) }

    val goals by viewModel.goals.collectAsState()

    // Calculate total saved and target
    val totalSaved = goals.sumOf { it.currentAmount }
    val totalTarget = goals.sumOf { it.targetAmount }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Green Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        .padding(top = 50.dp, bottom = 60.dp)
                ) {
                    // Header with title and add button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Savings Goals",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Track your Islamic financial goals",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        IconButton(
                            onClick = { showCreateGoalDialog = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Goal",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Total Summary Card (Inside green section)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Saved",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "RM ${String.format("%,d", totalSaved.toInt())}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Target",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "RM ${String.format("%,d", totalTarget.toInt())}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Goals List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-35).dp)
            ) {
                if (goals.isEmpty()) {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp)
                            .padding(top = 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // Title
                        Text(
                            text = "No Goals Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        Text(
                            text = "Start your savings journey by creating your first goal. Tap the \"+\" button above to get started!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    } else {
                        // Existing Goals List
                    goals.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            onEdit = {
                                selectedGoal = goal
                                showEditGoalDialog = true
                            },
                            onDelete = {
                                selectedGoal = goal
                                showDeleteGoalDialog = true
                            },
                            onAddContribution = {
                                selectedGoal = goal
                                showContributionDialog = true
                            },
                            onViewDetails = {
                                navController.navigate("goalDetails/${goal.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Create Goal Dialog
        if (showCreateGoalDialog) {
            CreateGoalDialog(
                onDismiss = { showCreateGoalDialog = false },
                onCreateGoal = { goal ->
                    viewModel.addGoal(
                        name = goal.name,
                        description = goal.description,
                        targetAmount = goal.targetAmount,
                        targetDate = goal.targetDate,
                        createdDate = goal.createdDate
                    )
                    showCreateGoalDialog = false
                }
            )
        }

        // Add Contribution Dialog
        if (showContributionDialog && selectedGoal != null) {
            AddContributionDialog(
                goal = selectedGoal!!,
                onDismiss = { showContributionDialog = false },
                onConfirm = { amount, isAddMoney, note ->
                    viewModel.addContribution(
                        goal = selectedGoal!!,
                        amount = amount,
                        isAdd = isAddMoney
                    )
                    showContributionDialog = false
                }
            )
        }

        // Edit Goal Dialog
        if (showEditGoalDialog && selectedGoal != null) {
            EditGoalDialog(
                goal = selectedGoal!!,
                onDismiss = { showEditGoalDialog = false },
                onUpdateGoal = { updatedGoal ->
                    viewModel.updateGoal(updatedGoal)
                    showEditGoalDialog = false
                }
            )
        }

        // Delete Goal Confirmation Dialog
        if (showDeleteGoalDialog && selectedGoal != null) {
            DeleteGoalConfirmationDialog(
                goalName = selectedGoal!!.name,
                onDismiss = { showDeleteGoalDialog = false },
                onConfirm = {
                    viewModel.deleteGoal(selectedGoal!!.id)
                    showDeleteGoalDialog = false
                    selectedGoal = null
                }
            )
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddContribution: () -> Unit,
    onViewDetails: () -> Unit // Add this parameter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Goal header with edit and delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Goal name with info icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = goal.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Image(
                            painter = painterResource(id = R.drawable.information),
                            contentDescription = "View Details",
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                                .clickable(onClick = onViewDetails)
                                .padding(7.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    /*Text(
                        text = goal.description,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )*/

                    // Show target date if available
                    if (goal.targetDate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Target Date",
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFF10B881)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target: ${goal.targetDate}",
                                fontSize = 12.sp,
                                color = Color(0xFF10B881),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color(0xFFF5F5F5),
                                shape = CircleShape
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color(0xFFFFE5E5),
                                shape = CircleShape
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${goal.progress}%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B881)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(goal.progress / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10B881))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount and date section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "RM ${String.format("%,d", goal.currentAmount.toInt())}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "of RM ${String.format("%,d", goal.targetAmount.toInt())}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Last contribution",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = goal.lastContribution,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Contribution button
            OutlinedButton(
                onClick = onAddContribution,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF10B881)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Color(0xFF10B881)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF10B881)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Contribution",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF10B881)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onCreateGoal: (Goal) -> Unit
) {
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                        .padding(24.dp)
                ) {
                    // Title
                    Text(
                        text = "Create New Goal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Goal Name
                    Text(
                        text = "Goal Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = goalName,
                        onValueChange = { goalName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "e.g., Umrah, Emergency Fund",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Amount
                    Text(
                        text = "Target Amount (RM)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                targetAmount = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "0",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Description",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Brief description of your goal",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Date
                    Text(
                        text = "Target Date (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        placeholder = {
                            Text(
                                "-/-/-",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = Color.Gray
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.LightGray,
                            disabledTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
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

                        // Create Goal Button
                        Button(
                            onClick = {
                                if (goalName.isNotEmpty() && targetAmount.isNotEmpty()) {
                                    val newGoal = Goal(
                                        id = UUID.randomUUID().toString(),
                                        name = goalName,
                                        description = description.ifEmpty { "No description" },
                                        currentAmount = 0.0,
                                        targetAmount = targetAmount.toDoubleOrNull() ?: 0.0,
                                        lastContribution = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                        progress = 0,
                                        targetDate = targetDate, // This should now save properly
                                        createdDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                        history = emptyList()
                                    )
                                    onCreateGoal(newGoal)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
                            ),
                            enabled = goalName.isNotEmpty() && targetAmount.isNotEmpty()
                        ) {
                            Text(
                                text = "Create Goal",
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

    // Material3 DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            targetDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF10B981))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF10B981),
                    todayContentColor = Color(0xFF10B981),
                    todayDateBorderColor = Color(0xFF10B981)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, isAddMoney: Boolean, note: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isAddMoney by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Calculate remaining amount that can be added
    val remainingToAdd = goal.targetAmount - goal.currentAmount
    val maxSubtract = goal.currentAmount

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
                        .padding(24.dp)
                ) {
                    // Title
                    Text(
                        text = "Manage Contribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(){
                        Text(
                            text = "Goal: ",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        // Goal Name
                        Text(
                            text = goal.name,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Transaction Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Toggle: Add or Withdraw
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Add Money Button
                        Button(
                            onClick = {
                                isAddMoney = true
                                showError = false
                                amount = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAddMoney) Color(0xFF10B981) else Color(0xFFF5F5F5),
                                contentColor = if (isAddMoney) Color.White else Color.Gray
                            )
                        ) {
                            Text(
                                text = "+ Add Money",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Withdraw Button
                        Button(
                            onClick = {
                                isAddMoney = false
                                showError = false
                                amount = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isAddMoney) Color(0xFFEF4444) else Color(0xFFF5F5F5),
                                contentColor = if (!isAddMoney) Color.White else Color.Gray
                            )
                        ) {
                            Text(
                                text = "- Subtract Money",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Amount Field
                    Text(
                        text = "Amount (RM)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                val parsedAmount = newValue.toDoubleOrNull() ?: 0.0

                                // Real-time validation
                                if (isAddMoney) {
                                    // Check if adding would exceed target
                                    if (parsedAmount > remainingToAdd) {
                                        showError = true
                                        errorMessage = "Maximum you can add: RM ${String.format("%,.2f", remainingToAdd)}"
                                    } else {
                                        showError = false
                                        amount = newValue
                                    }
                                } else {
                                    // Check if subtracting would go negative
                                    if (parsedAmount > maxSubtract) {
                                        showError = true
                                        errorMessage = "Maximum you can subtract: RM ${String.format("%,.2f", maxSubtract)}"
                                    } else {
                                        showError = false
                                        amount = newValue
                                    }
                                }

                                // Only update if valid or empty
                                if (!showError || newValue.isEmpty()) {
                                    amount = newValue
                                    if (newValue.isEmpty()) showError = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "0.00",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = showError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isAddMoney) Color(0xFF10B981) else Color(0xFFEF4444),
                            unfocusedBorderColor = Color.LightGray,
                            errorBorderColor = Color(0xFFEF4444)
                        )
                    )

                    // Error Message or Helper Text
                    Spacer(modifier = Modifier.height(4.dp))
                    if (showError) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    } else {
                        // Show max limits as helper text
                        if (isAddMoney) {
                            Text(
                                text = "Max amount you can add: RM ${String.format("%,.2f", remainingToAdd)}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else {
                            Text(
                                text = "Max amount you can subtract: RM ${String.format("%,.2f", maxSubtract)}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note Field
                    /*Text(
                        text = "Note (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Add a note...",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isAddMoney) Color(0xFF10B981) else Color(0xFFEF4444),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )*/

                    Spacer(modifier = Modifier.height(20.dp))

                    // Current Balance Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Current Balance:",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "RM ${String.format("%,.2f", goal.currentAmount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Target Amount:",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "RM ${String.format("%,.2f", goal.targetAmount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Remaining:",
                                    fontSize = 14.sp,
                                    color = Color(0xFF10B881)
                                )
                                Text(
                                    text = "RM ${String.format("%,.2f", remainingToAdd)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B881)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
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

                        // Confirm Button
                        Button(
                            onClick = {
                                val parsedAmount = amount.toDoubleOrNull() ?: 0.0

                                // Final validation before confirming
                                val isValid = when {
                                    parsedAmount <= 0 -> false
                                    !isAddMoney && parsedAmount > goal.currentAmount -> false
                                    isAddMoney && (goal.currentAmount + parsedAmount) > goal.targetAmount -> false
                                    else -> true
                                }

                                if (isValid) {
                                    onConfirm(parsedAmount, isAddMoney, note)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAddMoney) Color(0xFF10B981) else Color(0xFFEF4444),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            enabled = amount.isNotEmpty() &&
                                    amount.toDoubleOrNull() != null &&
                                    amount.toDouble() > 0 &&
                                    !showError
                        ) {
                            Text(
                                text = if (isAddMoney) "Add Money" else "Withdraw",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onUpdateGoal: (Goal) -> Unit
) {
    var goalName by remember { mutableStateOf(goal.name) }
    var targetAmount by remember { mutableStateOf(goal.targetAmount.toInt().toString()) }
    var description by remember { mutableStateOf(goal.description) }
    var targetDate by remember { mutableStateOf(goal.targetDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                        .padding(24.dp)
                ) {
                    // Title
                    Text(
                        text = "Edit Goal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Goal Name
                    Text(
                        text = "Goal Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = goalName,
                        onValueChange = { goalName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "e.g., Umrah, Emergency Fund",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Amount
                    Text(
                        text = "Target Amount (RM)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                targetAmount = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "0",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Description",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Brief description of your goal",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Date
                    Text(
                        text = "Target Date (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        placeholder = {
                            Text(
                                "DD/MM/YYYY",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = Color.Gray
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.LightGray,
                            disabledTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
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

                        // Update Goal Button
                        Button(
                            onClick = {
                                if (goalName.isNotEmpty() && targetAmount.isNotEmpty()) {
                                    val updatedGoal = goal.copy(
                                        name = goalName,
                                        description = description.ifEmpty { "No description" },
                                        targetAmount = targetAmount.toDoubleOrNull() ?: goal.targetAmount,
                                        targetDate = targetDate // Keep the updated or existing targetDate
                                    )
                                    // Recalculate progress with new target amount
                                    val newProgress = ((updatedGoal.currentAmount / updatedGoal.targetAmount) * 100).toInt().coerceIn(0, 100)
                                    onUpdateGoal(updatedGoal.copy(progress = newProgress))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
                            ),
                            enabled = goalName.isNotEmpty() && targetAmount.isNotEmpty()
                        ) {
                            Text(
                                text = "Update Goal",
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

    // Material3 DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            targetDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF10B981))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF10B981),
                    todayContentColor = Color(0xFF10B981),
                    todayDateBorderColor = Color(0xFF10B981)
                )
            )
        }
    }
}

@Composable
fun DeleteGoalConfirmationDialog(
    goalName: String,
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
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Delete Goal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = "Are you sure you want to delete $goalName goal? This action cannot be undone.",
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

                        // Delete Button
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
                                text = "Delete",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GoalsScreenPreview() {
    HalalFinanceTheme {
        GoalsScreen(navController = rememberNavController())
    }
}