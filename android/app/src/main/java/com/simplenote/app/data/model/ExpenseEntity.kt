package com.simplenote.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String = "exp_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(4),
    val amount: Long,
    val note: String = "",
    val timestamp: String,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String = "sparkles"
)
