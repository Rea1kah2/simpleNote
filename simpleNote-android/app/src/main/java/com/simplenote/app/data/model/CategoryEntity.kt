package com.simplenote.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String = "sparkles",
    val isDefault: Boolean = false,
    val type: String = "expense"
)
