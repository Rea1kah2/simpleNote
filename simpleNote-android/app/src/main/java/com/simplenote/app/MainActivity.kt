package com.simplenote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.ui.components.AddExpenseSheet
import com.simplenote.app.ui.components.BottomFloatingToastCard
import com.simplenote.app.ui.components.CategoryManagerDialog
import com.simplenote.app.ui.components.EditExpenseSheet
import com.simplenote.app.ui.screens.history.HistoryScreen
import com.simplenote.app.ui.screens.settings.SettingsScreen
import com.simplenote.app.ui.screens.stats.StatsScreen
import com.simplenote.app.ui.screens.today.TodayScreen
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.SimpleNoteTheme
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaLight
import com.simplenote.app.ui.theme.TerracottaPrimary
import com.simplenote.app.viewmodel.MainViewModel
import com.simplenote.app.viewmodel.MainViewModelFactory

enum class AppTab(val title: String, val icon: ImageVector) {
    TODAY("Hari Ini", Icons.Outlined.Receipt),
    HISTORY("Riwayat", Icons.Outlined.CalendarMonth),
    STATS("Statistik", Icons.Outlined.BarChart),
    SETTINGS("Pengaturan", Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SimpleNoteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleNoteTheme {
                SimpleNoteApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.triggerBackgroundAutoSync()
    }
}

@Composable
fun SimpleNoteApp(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.TODAY) }

    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val toastMessage by viewModel.toast.collectAsState()
    val isAddSheetOpen by viewModel.isAddSheetOpen.collectAsState()
    val editingExpense by viewModel.editingExpense.collectAsState()
    val isCategoryManagerOpen by viewModel.isCategoryManagerOpen.collectAsState()
    val cloudUrl by viewModel.cloudUrl.collectAsState()
    val isAutoSyncEnabled by viewModel.isAutoSyncEnabled.collectAsState()
    val lastSyncStatus by viewModel.lastSyncStatus.collectAsState()

    Scaffold(
        containerColor = PaperBackground,
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .background(BorderBeige)
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) TerracottaPrimary else StoneMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TerracottaPrimary else StoneMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = TerracottaLight,
                            selectedIconColor = TerracottaPrimary,
                            unselectedIconColor = StoneMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                AppTab.TODAY -> {
                    TodayScreen(
                        expenses = expenses,
                        onAddExpenseClick = { viewModel.isAddSheetOpen.value = true },
                        onExpenseClick = { viewModel.editingExpense.value = it }
                    )
                }
                AppTab.HISTORY -> {
                    HistoryScreen(
                        expenses = expenses,
                        categories = categories,
                        onExpenseClick = { viewModel.editingExpense.value = it }
                    )
                }
                AppTab.STATS -> {
                    StatsScreen(
                        expenses = expenses,
                        onExpenseClick = { viewModel.editingExpense.value = it }
                    )
                }
                AppTab.SETTINGS -> {
                    SettingsScreen(
                        totalRecords = expenses.size,
                        cloudUrl = cloudUrl,
                        isAutoSyncEnabled = isAutoSyncEnabled,
                        lastSyncStatus = lastSyncStatus,
                        onSaveCloudUrl = { viewModel.setCloudUrl(it) },
                        onToggleAutoSync = { viewModel.setAutoSyncEnabled(it) },
                        onTestConnection = { viewModel.testConnection(it) },
                        onOpenCategoryManager = { viewModel.isCategoryManagerOpen.value = true },
                        onExportJson = { viewModel.getExportJson() },
                        onImportJson = { viewModel.importBackup(it) },
                        onSyncServer = { viewModel.syncWithServer(it) },
                        onResetData = { viewModel.clearAllData() }
                    )
                }
            }

            // Bottom Toast Notification floating above NavigationBar
            BottomFloatingToastCard(
                toast = toastMessage,
                onDismiss = { viewModel.dismissToast() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }

        // Add Expense Sheet
        if (isAddSheetOpen) {
            AddExpenseSheet(
                categories = categories,
                onDismiss = { viewModel.isAddSheetOpen.value = false },
                onSaveExpense = { amount, category, note, timestamp ->
                    viewModel.addExpense(amount, category, note, timestamp)
                }
            )
        }

        // Edit Expense Sheet
        editingExpense?.let { expenseToEdit ->
            EditExpenseSheet(
                expense = expenseToEdit,
                categories = categories,
                onDismiss = { viewModel.editingExpense.value = null },
                onSaveEdit = { updated ->
                    viewModel.updateExpense(updated)
                },
                onDeleteExpense = { toDelete ->
                    viewModel.deleteExpense(toDelete)
                }
            )
        }

        // Category Manager Dialog
        if (isCategoryManagerOpen) {
            CategoryManagerDialog(
                categories = categories,
                onDismiss = { viewModel.isCategoryManagerOpen.value = false },
                onAddCategory = { name, icon ->
                    viewModel.addCategory(name, icon)
                },
                onDeleteCategory = { cat ->
                    viewModel.deleteCategory(cat)
                }
            )
        }
    }
}
