package com.simplenote.app

import android.app.Application
import com.simplenote.app.data.local.CloudPreferences
import com.simplenote.app.data.local.SimpleNoteDatabase
import com.simplenote.app.data.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SimpleNoteApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val cloudPreferences by lazy { CloudPreferences(this) }
    val database by lazy { SimpleNoteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ExpenseRepository(database.expenseDao(), database.categoryDao(), cloudPreferences) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            repository.ensureDefaultCategories()
        }
    }
}
