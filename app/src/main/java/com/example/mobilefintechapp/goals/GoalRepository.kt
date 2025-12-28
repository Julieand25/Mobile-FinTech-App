package com.example.mobilefintechapp.goals

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GoalRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun goalsRef() =
        auth.currentUser?.uid?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("goals")
        } ?: throw IllegalStateException("User not logged in")

    suspend fun addGoal(goal: Goal) {
        val docRef = goalsRef().document()
        docRef.set(goal.copy(id = docRef.id)).await()
    }

    suspend fun updateGoal(goal: Goal) {
        goalsRef().document(goal.id).set(goal).await()
    }

    suspend fun deleteGoal(goalId: String) {
        goalsRef().document(goalId).delete().await()
    }
}

