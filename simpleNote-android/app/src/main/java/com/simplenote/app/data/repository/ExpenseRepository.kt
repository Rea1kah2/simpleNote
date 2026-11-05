package com.simplenote.app.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.simplenote.app.data.local.CategoryDao
import com.simplenote.app.data.local.ExpenseDao
import com.simplenote.app.data.local.SimpleNoteDatabase
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.data.model.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun insertExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: String) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun deleteAllExpenses() = withContext(Dispatchers.IO) {
        expenseDao.deleteAllExpenses()
    }

    suspend fun insertCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category)
    }

    suspend fun ensureDefaultCategories() = withContext(Dispatchers.IO) {
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertAll(SimpleNoteDatabase.DEFAULT_CATEGORIES)
        }
    }

    // MARK: - JSON Export
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val expenses = allExpenses.first()
        val categories = allCategories.first()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val nowIso = sdf.format(Date())

        val payload = mapOf(
            "version" to "1.3.0",
            "exportDate" to nowIso,
            "totalExpenses" to expenses.size,
            "categories" to categories,
            "expenses" to expenses
        )
        gson.toJson(payload)
    }

    // MARK: - JSON Import
    suspend fun importBackupJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            if (!jsonObject.has("expenses")) {
                return@withContext Result.failure(IllegalArgumentException("Format file tidak valid (tidak ada field expenses)"))
            }

            val expensesType = object : com.google.gson.reflect.TypeToken<List<ExpenseEntity>>() {}.type
            val importedExpenses: List<ExpenseEntity> = gson.fromJson(jsonObject.get("expenses"), expensesType) ?: emptyList()

            if (jsonObject.has("categories")) {
                val categoriesType = object : com.google.gson.reflect.TypeToken<List<CategoryEntity>>() {}.type
                val importedCategories: List<CategoryEntity>? = gson.fromJson(jsonObject.get("categories"), categoriesType)
                if (!importedCategories.isNullOrEmpty()) {
                    categoryDao.insertAll(importedCategories)
                }
            }

            if (importedExpenses.isNotEmpty()) {
                expenseDao.insertAll(importedExpenses)
            }

            Result.success(importedExpenses.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK: - Local Wi-Fi Server Sync
    suspend fun syncWithLocalServer(serverIpPort: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = if (serverIpPort.startsWith("http://") || serverIpPort.startsWith("https://")) {
                "$serverIpPort/api/data"
            } else {
                "http://$serverIpPort/api/data"
            }

            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonObject = gson.fromJson(response, JsonObject::class.java)
                val expensesType = object : com.google.gson.reflect.TypeToken<List<ExpenseEntity>>() {}.type
                val serverExpenses: List<ExpenseEntity> = gson.fromJson(jsonObject.get("expenses"), expensesType) ?: emptyList()

                val localExpenses = allExpenses.first()
                if (serverExpenses.isEmpty() && localExpenses.isNotEmpty()) {
                    // Push to server
                    pushDataToServer(endpoint, localExpenses, allCategories.first())
                    Result.success("Tersinkron: ${localExpenses.size} catatan diunggah ke laptop")
                } else {
                    // Merge local and server
                    if (serverExpenses.isNotEmpty()) {
                        expenseDao.insertAll(serverExpenses)
                    }
                    if (jsonObject.has("categories")) {
                        val categoriesType = object : com.google.gson.reflect.TypeToken<List<CategoryEntity>>() {}.type
                        val serverCats: List<CategoryEntity>? = gson.fromJson(jsonObject.get("categories"), categoriesType)
                        if (!serverCats.isNullOrEmpty()) {
                            categoryDao.insertAll(serverCats)
                        }
                    }
                    Result.success("Tersinkron: ${serverExpenses.size} catatan diperbarui dari laptop")
                }
            } else {
                Result.failure(Exception("HTTP error: ${conn.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun pushDataToServer(endpoint: String, expenses: List<ExpenseEntity>, categories: List<CategoryEntity>) {
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 4000
        conn.readTimeout = 4000

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val nowIso = sdf.format(Date())

        val payload = mapOf(
            "expenses" to expenses,
            "categories" to categories,
            "lastUpdated" to nowIso
        )
        val writer = OutputStreamWriter(conn.outputStream)
        writer.write(gson.toJson(payload))
        writer.flush()
        writer.close()
        conn.responseCode
    }
}
