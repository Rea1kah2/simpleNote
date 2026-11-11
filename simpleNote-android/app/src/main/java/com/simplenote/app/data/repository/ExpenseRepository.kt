package com.simplenote.app.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.simplenote.app.data.local.CategoryDao
import com.simplenote.app.data.local.CloudPreferences
import com.simplenote.app.data.local.ExpenseDao
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
    private val categoryDao: CategoryDao,
    val cloudPreferences: CloudPreferences
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val expenseCategories: Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType("expense")
    val incomeCategories: Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType("income")

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
        val current = categoryDao.getAllCategories().first()
        if (current.isEmpty()) {
            val defaults = com.simplenote.app.data.local.SimpleNoteDatabase.DEFAULT_CATEGORIES +
                    com.simplenote.app.data.local.SimpleNoteDatabase.DEFAULT_INCOME_CATEGORIES
            categoryDao.insertAll(defaults)
        } else {
            val hasIncome = current.any { it.type == "income" }
            if (!hasIncome) {
                categoryDao.insertAll(com.simplenote.app.data.local.SimpleNoteDatabase.DEFAULT_INCOME_CATEGORIES)
            }
        }
    }

    // MARK: - JSON Export & Import
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val expensesList = allExpenses.first()
        val categoriesList = allCategories.first()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val nowIso = sdf.format(Date())

        val payload = mapOf(
            "expenses" to expensesList,
            "categories" to categoriesList,
            "lastUpdated" to nowIso
        )
        gson.toJson(payload)
    }

    suspend fun importBackupJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

            val expensesType = object : com.google.gson.reflect.TypeToken<List<ExpenseEntity>>() {}.type
            val importedExpenses: List<ExpenseEntity>? = gson.fromJson(jsonObject.get("expenses"), expensesType)

            if (jsonObject.has("categories")) {
                val categoriesType = object : com.google.gson.reflect.TypeToken<List<CategoryEntity>>() {}.type
                val importedCategories: List<CategoryEntity>? = gson.fromJson(jsonObject.get("categories"), categoriesType)
                if (!importedCategories.isNullOrEmpty()) {
                    categoryDao.insertAll(importedCategories)
                }
            }

            if (importedExpenses.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("File cadangan tidak berisi pengeluaran"))
            }

            expenseDao.insertAll(importedExpenses)
            Result.success(importedExpenses.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK: - Smart Cloud & Server Sync
    fun sanitizeEndpoint(serverAddress: String): String {
        var trimmed = serverAddress.trim().trimEnd('/')
        if (trimmed.isEmpty()) return ""
        if (trimmed.endsWith("/api/data")) return trimmed

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = if (trimmed.contains(".vercel.app") || (!trimmed.contains(":") && trimmed.contains("."))) {
                "https://$trimmed"
            } else {
                "http://$trimmed"
            }
        }
        return "$trimmed/api/data"
    }

    suspend fun testServerConnection(serverAddress: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = sanitizeEndpoint(serverAddress)
            if (endpoint.isEmpty()) return@withContext Result.failure(Exception("Alamat URL tidak boleh kosong"))

            val pingUrl = if (endpoint.contains("?")) "$endpoint&ping=true" else "$endpoint?ping=true"
            val url = URL(pingUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            val code = conn.responseCode
            if (code in 200..299) {
                val backend = conn.getHeaderField("X-Storage-Backend") ?: "Online"
                Result.success("Terhubung ($backend)")
            } else {
                Result.failure(Exception("HTTP Error $code"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal menghubungi server cloud"))
        }
    }

    suspend fun syncWithLocalServer(serverIpPort: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = sanitizeEndpoint(serverIpPort)
            if (endpoint.isEmpty()) return@withContext Result.failure(Exception("Alamat URL kosong"))

            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            if (conn.responseCode in 200..299) {
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
                    cloudPreferences.lastSyncTime = System.currentTimeMillis()
                    cloudPreferences.lastSyncStatus = "Sukses (${localExpenses.size} diunggah)"
                    Result.success("Tersinkron: ${localExpenses.size} catatan diunggah ke cloud")
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

                    // If local had items not yet on server, push merged union to server
                    val mergedExpenses = allExpenses.first()
                    if (mergedExpenses.size > serverExpenses.size) {
                        pushDataToServer(endpoint, mergedExpenses, allCategories.first())
                    }

                    cloudPreferences.lastSyncTime = System.currentTimeMillis()
                    cloudPreferences.lastSyncStatus = "Sukses (${mergedExpenses.size} transaksi)"
                    Result.success("Tersinkron: ${mergedExpenses.size} transaksi aktif")
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
        conn.connectTimeout = 6000
        conn.readTimeout = 6000

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
