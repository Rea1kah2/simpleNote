package com.simplenote.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.data.repository.ExpenseRepository
import com.simplenote.app.ui.components.ToastMessage
import com.simplenote.app.ui.components.ToastType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    private var toastJob: Job? = null

    var editingExpense = MutableStateFlow<ExpenseEntity?>(null)
    var isAddSheetOpen = MutableStateFlow(false)
    var isCategoryManagerOpen = MutableStateFlow(false)

    fun showToast(type: ToastType, title: String, message: String) {
        toastJob?.cancel()
        _toast.value = ToastMessage(type, title, message)
        toastJob = viewModelScope.launch {
            delay(4000)
            _toast.value = null
        }
    }

    fun dismissToast() {
        toastJob?.cancel()
        _toast.value = null
    }

    fun addExpense(amount: Long, category: CategoryEntity, note: String, timestamp: String) {
        viewModelScope.launch {
            val entity = ExpenseEntity(
                amount = amount,
                note = note,
                timestamp = timestamp,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.icon
            )
            repository.insertExpense(entity)
            isAddSheetOpen.value = false
            showToast(ToastType.SUCCESS, "Pengeluaran Dicatat", "${category.name} - Rp $amount")
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            editingExpense.value = null
            showToast(ToastType.INFO, "Perubahan Disimpan", "${expense.categoryName} diperbarui")
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            editingExpense.value = null
            showToast(ToastType.DANGER, "Catatan Dihapus", "${expense.categoryName} telah dihapus")
        }
    }

    fun addCategory(name: String, icon: String) {
        viewModelScope.launch {
            val newCat = CategoryEntity(
                id = "cat_custom_" + System.currentTimeMillis(),
                name = name,
                icon = icon,
                isDefault = false
            )
            repository.insertCategory(newCat)
            showToast(ToastType.SUCCESS, "Kategori Ditambahkan", "Kategori \"$name\" siap digunakan")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            showToast(ToastType.DANGER, "Kategori Dihapus", "Kategori \"${category.name}\" telah dihapus")
        }
    }

    suspend fun getExportJson(): String {
        return repository.exportBackupJson()
    }

    fun importBackup(jsonString: String) {
        viewModelScope.launch {
            val result = repository.importBackupJson(jsonString)
            if (result.isSuccess) {
                val count = result.getOrNull() ?: 0
                showToast(ToastType.SUCCESS, "Cadangan Dipulihkan", "$count transaksi berhasil dimuat")
            } else {
                showToast(ToastType.DANGER, "Gagal Memulihkan", result.exceptionOrNull()?.message ?: "Format file salah")
            }
        }
    }

    fun syncWithServer(serverAddress: String) {
        viewModelScope.launch {
            showToast(ToastType.INFO, "Menghubungkan Server...", "Mengontak $serverAddress")
            val result = repository.syncWithLocalServer(serverAddress)
            if (result.isSuccess) {
                showToast(ToastType.SUCCESS, "Sinkronisasi Berhasil", result.getOrNull() ?: "Tersinkron")
            } else {
                showToast(ToastType.DANGER, "Gagal Sinkronisasi", result.exceptionOrNull()?.localizedMessage ?: "Server tidak dapat dijangkau")
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllExpenses()
            showToast(ToastType.DANGER, "Data Dibersihkan", "Semua riwayat transaksi telah dihapus")
        }
    }
}

class MainViewModelFactory(
    private val repository: ExpenseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
