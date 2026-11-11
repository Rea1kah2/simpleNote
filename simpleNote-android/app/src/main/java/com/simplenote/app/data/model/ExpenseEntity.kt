package com.simplenote.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
    val categoryIcon: String = "sparkles",
    val updatedAt: String? = null,
    @ColumnInfo(defaultValue = "expense")
    val type: String = "expense"
) {
    fun parsedDate(): Date? = parseUtcIso(timestamp)
    fun parsedUpdatedDate(): Date? = updatedAt?.let { parseUtcIso(it) }

    companion object {
        fun parseUtcIso(iso: String): Date? {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                parser.parse(iso.take(19))
            } catch (e: Exception) {
                null
            }
        }
    }
}
