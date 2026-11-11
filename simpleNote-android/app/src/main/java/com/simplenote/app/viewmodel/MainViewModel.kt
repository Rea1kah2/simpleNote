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

    val expenseCategories: StateFlow<List<CategoryEntity>> = repository.expenseCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<CategoryEntity>> = repository.incomeCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    private var toastJob: Job? = null

    var editingExpense = MutableStateFlow<ExpenseEntity?>(null)
    var isAddSheetOpen = MutableStateFlow(false)
    var addMode = MutableStateFlow("expense") // "expense" | "income"
    var targetAddDate = MutableStateFlow<Date?>(null)
    var isCategoryManagerOpen = MutableStateFlow(false)

    fun openAddExpense(date: Date? = null) {
        addMode.value = "expense"
        targetAddDate.value = date
        isAddSheetOpen.value = true
    }

    fun openAddIncome(date: Date? = null) {
        addMode.value = "income"
        targetAddDate.value = date
        isAddSheetOpen.value = true
    }

    fun closeAddExpense() {
        targetAddDate.value = null
        isAddSheetOpen.value = false
    }

    // Cloud & Sync state
    val cloudUrl = MutableStateFlow(repository.cloudPreferences.cloudUrl)
    val isAutoSyncEnabled = MutableStateFlow(repository.cloudPreferences.isAutoSyncEnabled)
    val lastSyncStatus = MutableStateFlow(repository.cloudPreferences.lastSyncStatus)
    val isSyncing = MutableStateFlow(false)

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

    fun setCloudUrl(url: String) {
        val trimmed = url.trim()
        repository.cloudPreferences.cloudUrl = trimmed
        cloudUrl.value = trimmed
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        repository.cloudPreferences.isAutoSyncEnabled = enabled
        isAutoSyncEnabled.value = enabled
    }

    fun addExpense(amount: Long, category: CategoryEntity, note: String, timestamp: String, type: String = "expense") {
        viewModelScope.launch {
            val entity = ExpenseEntity(
                amount = amount,
                note = note,
                timestamp = timestamp,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.icon,
                type = type
            )
            repository.insertExpense(entity)
            closeAddExpense()
            val label = if (type == "income") "Pemasukan Dicatat" else "Pengeluaran Dicatat"
            showToast(ToastType.SUCCESS, label, "${category.name} - Rp $amount")

            // If auto-sync is on, push update in background
            if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                triggerBackgroundAutoSync()
            }
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            editingExpense.value = null
            showToast(ToastType.INFO, "Perubahan Disimpan", "${expense.categoryName} diperbarui")

            if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                triggerBackgroundAutoSync()
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            editingExpense.value = null
            showToast(ToastType.DANGER, "Catatan Dihapus", "${expense.categoryName} telah dihapus")

            if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                triggerBackgroundAutoSync()
            }
        }
    }

    fun addCategory(name: String, icon: String, type: String = "expense") {
        viewModelScope.launch {
            val prefix = if (type == "income") "inc_custom_" else "cat_custom_"
            val newCat = CategoryEntity(
                id = prefix + System.currentTimeMillis(),
                name = name,
                icon = icon,
                isDefault = false,
                type = type
            )
            repository.insertCategory(newCat)
            showToast(ToastType.SUCCESS, "Kategori Ditambahkan", "Kategori \"$name\" siap digunakan")

            if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                triggerBackgroundAutoSync()
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            showToast(ToastType.DANGER, "Kategori Dihapus", "Kategori \"${category.name}\" telah dihapus")

            if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                triggerBackgroundAutoSync()
            }
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
                if (isAutoSyncEnabled.value && cloudUrl.value.isNotBlank()) {
                    triggerBackgroundAutoSync()
                }
            } else {
                showToast(ToastType.DANGER, "Gagal Memulihkan", result.exceptionOrNull()?.message ?: "Format file salah")
            }
        }
    }

    fun testConnection(serverAddress: String) {
        viewModelScope.launch {
            setCloudUrl(serverAddress)
            showToast(ToastType.INFO, "Menguji Koneksi...", "Menghubungi server cloud...")
            val result = repository.testServerConnection(serverAddress)
            if (result.isSuccess) {
                val msg = result.getOrNull() ?: "Terhubung"
                showToast(ToastType.SUCCESS, "Koneksi Berhasil", msg)
            } else {
                showToast(ToastType.DANGER, "Gagal Menghubungi", result.exceptionOrNull()?.localizedMessage ?: "Server tidak dapat dijangkau")
            }
        }
    }

    fun syncWithServer(serverAddress: String, isSilent: Boolean = false) {
        if (isSyncing.value) return
        isSyncing.value = true

        viewModelScope.launch {
            setCloudUrl(serverAddress)
            if (!isSilent) {
                showToast(ToastType.INFO, "Menghubungkan Server...", "Menyinkronkan data dengan cloud...")
            }
            val result = repository.syncWithLocalServer(serverAddress)
            isSyncing.value = false

            if (result.isSuccess) {
                val msg = result.getOrNull() ?: "Tersinkron"
                lastSyncStatus.value = repository.cloudPreferences.lastSyncStatus
                if (!isSilent) {
                    showToast(ToastType.SUCCESS, "Sinkronisasi Berhasil", msg)
                }
            } else {
                if (!isSilent) {
                    showToast(ToastType.DANGER, "Gagal Sinkronisasi", result.exceptionOrNull()?.localizedMessage ?: "Server tidak dapat dijangkau")
                }
            }
        }
    }

    fun triggerBackgroundAutoSync() {
        val targetUrl = cloudUrl.value
        if (isAutoSyncEnabled.value && targetUrl.isNotBlank() && !isSyncing.value) {
            syncWithServer(targetUrl, isSilent = true)
        }
    }

    fun hasDeletePassword(): Boolean = repository.cloudPreferences.hasDeletePassword()

    fun setDeletePassword(plainText: String) {
        repository.cloudPreferences.setDeletePassword(plainText)
    }

    fun verifyDeletePassword(plainText: String): Boolean {
        return repository.cloudPreferences.verifyDeletePassword(plainText)
    }

    fun changeDeletePassword(oldPass: String, newPass: String): Boolean {
        if (verifyDeletePassword(oldPass)) {
            setDeletePassword(newPass)
            showToast(ToastType.SUCCESS, "Password Diperbarui", "Password rahasia berhasil diubah")
            return true
        } else {
            showToast(ToastType.DANGER, "Gagal Ubah Password", "Password lama tidak sesuai")
            return false
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
