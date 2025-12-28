package com.example.mobilefintechapp.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GoalsViewModel : ViewModel() {

    private val repository = GoalRepository()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    init {
        observeGoals()
    }

    private fun observeGoals() {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("goals")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    _goals.value = it.documents.mapNotNull { doc ->
                        doc.toObject(Goal::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    fun addGoal(name: String, description: String, targetAmount: Double, targetDate: String = "", createdDate: String = "") {
        val goal = Goal(
            name = name,
            description = description,
            targetAmount = targetAmount,
            currentAmount = 0.0,
            lastContribution = today(),
            progress = 0,
            targetDate = targetDate,
            createdDate = createdDate.ifEmpty { today() },
            history = emptyList()
        )

        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        val progress =
            if (goal.targetAmount > 0)
                ((goal.currentAmount / goal.targetAmount) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            else 0

        viewModelScope.launch {
            repository.updateGoal(goal.copy(progress = progress))
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    fun addContribution(goal: Goal, amount: Double, isAdd: Boolean, note: String = "") {
        val newAmount = if (isAdd)
            goal.currentAmount + amount
        else
            (goal.currentAmount - amount).coerceAtLeast(0.0)

        // Create transaction record
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            amount = amount,
            type = if (isAdd) "add" else "subtract",
            note = note,
            date = today(),
            timestamp = System.currentTimeMillis()
        )

        // Add transaction to history
        val updatedHistory = goal.history + transaction

        val updatedGoal = goal.copy(
            currentAmount = newAmount,
            lastContribution = today(),
            history = updatedHistory
        )

        updateGoal(updatedGoal)
    }

    private fun today(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
}
