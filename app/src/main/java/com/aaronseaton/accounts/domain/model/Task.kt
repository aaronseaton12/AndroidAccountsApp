package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Task(
    @DocumentId
    override val documentID: String = "",
    val name: String = "",
    val description: String = "",
    val subTasks: MutableList<String> = mutableListOf(),
    val dueDate: Date = Calendar.getInstance().time,
    val completedDate: Date? = null,
    val done: Boolean = false,
    val assignedTo: String = "", //UserID
    val wasCreatedBy: String = "", //UserID
    val estimatedCost: Double = 0.0,
    val matter: String? = null
) : FirebaseEntity