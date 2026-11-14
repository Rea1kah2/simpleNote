package com.simplenote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import com.simplenote.app.ui.screens.income.IncomeScreen
import com.simplenote.app.ui.screens.settings.SettingsScreen
import com.simplenote.app.ui.screens.stats.StatsScreen
import com.simplenote.app.ui.screens.today.TodayScreen
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.EmeraldLight
import com.simplenote.app.ui.theme.EmeraldPrimary
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.SimpleNoteTheme
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaLight
import com.simplenote.app.ui.theme.TerracottaPrimary
import com.simplenote.app.viewmodel.MainViewModel
import com.simplenote.app.viewmodel.MainViewModelFactory

enum class AppSection {
    EXPENSE, INCOME
}

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
    var currentSection by remember { mutableStateOf(AppSection.EXPENSE) }
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
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaperBackground)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SimpleNote",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CharcoalInk,
                        letterSpacing = (-0.5).sp
                    )

                    // Global Switcher Pill: [ Pengeluaran | Pemasukan ]
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSurface)
                            .border(1.dp, BorderBeige, RoundedCornerShape(20.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val isExpense = currentSection == AppSection.EXPENSE
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isExpense) TerracottaPrimary else Color.Transparent)
                                .clickable { currentSection = AppSection.EXPENSE }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pengeluaran",
                                fontSize = 12.sp,
                                fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                                color = if (isExpense) Color.White else StoneMuted
                            )
                        }

                        val isIncome = currentSection == AppSection.INCOME
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isIncome) EmeraldPrimary else Color.Transparent)
                                .clickable { currentSection = AppSection.INCOME }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pemasukan",
                                fontSize = 12.sp,
                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                                color = if (isIncome) Color.White else StoneMuted
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderBeige.copy(alpha = 0.5f))
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .background(BorderBeige)
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val activeColor = if (currentSection == AppSection.INCOME) EmeraldPrimary else TerracottaPrimary
                    val activeIndicator = if (currentSection == AppSection.INCOME) EmeraldLight else TerracottaLight
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentTab = tab
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) activeColor else StoneMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) activeColor else StoneMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = activeIndicator,
                            selectedIconColor = activeColor,
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
                    if (currentSection == AppSection.INCOME) {
                        IncomeScreen(
                            expenses = expenses,
                            onAddIncomeClick = { viewModel.openAddIncome() },
                            onIncomeClick = { viewModel.editingExpense.value = it }
                        )
                    } else {
                        TodayScreen(
                            expenses = expenses,
                            onAddExpenseClick = { viewModel.openAddExpense() },
                            onExpenseClick = { viewModel.editingExpense.value = it }
                        )
                    }
                }
                AppTab.HISTORY -> {
                    HistoryScreen(
                        expenses = expenses,
                        categories = categories,
                        activeSection = currentSection,
                        onExpenseClick = { viewModel.editingExpense.value = it },
                        onAddExpenseForDate = { date ->
                            viewModel.openAddExpense(date)
                        },
                        onAddIncomeForDate = { date ->
                            viewModel.openAddIncome(date)
                        }
                    )
                }
                AppTab.STATS -> {
                    StatsScreen(
                        expenses = expenses,
                        activeSection = currentSection,
                        onExpenseClick = { viewModel.editingExpense.value = it }
                    )
                }
                AppTab.SETTINGS -> {
                        SettingsScreen(
                            totalRecords = expenses.size,
                            cloudUrl = cloudUrl,
                            isAutoSyncEnabled = isAutoSyncEnabled,
                            lastSyncStatus = lastSyncStatus,
                            hasDeletePassword = viewModel.hasDeletePassword(),
                            onSaveCloudUrl = { viewModel.setCloudUrl(it) },
                            onToggleAutoSync = { viewModel.setAutoSyncEnabled(it) },
                            onTestConnection = { viewModel.testConnection(it) },
                            onOpenCategoryManager = { viewModel.isCategoryManagerOpen.value = true },
                            onExportJson = { viewModel.getExportJson() },
                            onImportJson = { viewModel.importBackup(it) },
                            onSyncServer = { viewModel.syncWithServer(it) },
                            onSetDeletePassword = { viewModel.setDeletePassword(it) },
                            onVerifyDeletePassword = { viewModel.verifyDeletePassword(it) },
                            onChangeDeletePassword = { oldPass, newPass -> viewModel.changeDeletePassword(oldPass, newPass) },
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

        // Add Transaction Sheet (Expense or Income)
        if (isAddSheetOpen) {
            val targetAddDate by viewModel.targetAddDate.collectAsState()
            val addMode by viewModel.addMode.collectAsState()
            val expenseCategories by viewModel.expenseCategories.collectAsState()
            val incomeCategories by viewModel.incomeCategories.collectAsState()
            val activeCategories = if (addMode == "income") incomeCategories else expenseCategories

            AddExpenseSheet(
                categories = activeCategories,
                mode = addMode,
                initialDate = targetAddDate,
                onDismiss = { viewModel.closeAddExpense() },
                onSaveExpense = { amount, category, note, timestamp, type ->
                    viewModel.addExpense(amount, category, note, timestamp, type)
                }
            )
        }

        // Edit Expense Sheet
        editingExpense?.let { expenseToEdit ->
            val relevantCategories = if (expenseToEdit.type == "income") {
                categories.filter { it.type == "income" }
            } else {
                categories.filter { it.type != "income" }
            }
            EditExpenseSheet(
                expense = expenseToEdit,
                categories = relevantCategories.ifEmpty { categories },
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
                onAddCategory = { name, icon, type ->
                    viewModel.addCategory(name, icon, type)
                },
                onDeleteCategory = { cat ->
                    viewModel.deleteCategory(cat)
                }
            )
        }
    }
}
