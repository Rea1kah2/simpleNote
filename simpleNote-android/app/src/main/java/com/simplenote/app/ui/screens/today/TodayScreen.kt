package com.simplenote.app.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.ui.components.AppIcon
import com.simplenote.app.ui.components.formatRupiah
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.EmeraldDark
import com.simplenote.app.ui.theme.EmeraldLight
import com.simplenote.app.ui.theme.EmeraldPrimary
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaDark
import com.simplenote.app.ui.theme.TerracottaLight
import com.simplenote.app.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    expenses: List<ExpenseEntity>,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit = {},
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    var activeMode by remember { mutableStateOf("expense") } // "expense" | "income"

    val todayAll = remember(expenses) {
        val todayCal = Calendar.getInstance()
        expenses.filter { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        c.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
            } else false
        }
    }

    val todayExpenseList = remember(todayAll) { todayAll.filter { it.type != "income" } }
    val todayIncomeList = remember(todayAll) { todayAll.filter { it.type == "income" } }

    val todayExpenseTotal = remember(todayExpenseList) { todayExpenseList.sumOf { it.amount } }
    val todayIncomeTotal = remember(todayIncomeList) { todayIncomeList.sumOf { it.amount } }
    val todayNetBalance = remember(todayIncomeTotal, todayExpenseTotal) { todayIncomeTotal - todayExpenseTotal }

    val displayedList = if (activeMode == "income") todayIncomeList else todayExpenseList

    val todayFullDate = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        sdf.format(Date())
    }

    Scaffold(
        containerColor = PaperBackground,
        floatingActionButton = {
            val isIncomeMode = activeMode == "income"
            FloatingActionButton(
                onClick = {
                    if (isIncomeMode) onAddIncomeClick() else onAddExpenseClick()
                },
                containerColor = if (isIncomeMode) EmeraldPrimary else TerracottaPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isIncomeMode) "Tambah Pemasukan" else "Tambah Pengeluaran",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Canvas Hero Header (Operate Mode: Clean Net Balance)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HARI INI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TerracottaPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = " • ",
                            fontSize = 12.sp,
                            color = StoneMuted
                        )
                        Text(
                            text = todayFullDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = StoneMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Saldo Bersih Hari Ini",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StoneMuted
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val netPrefix = if (todayNetBalance > 0) "+ " else ""
                    Text(
                        text = "$netPrefix${formatRupiah(todayNetBalance)}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalInk,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Signature Accent Bar (Emerald if positive, Terracotta if negative/zero)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (todayNetBalance >= 0) EmeraldPrimary else TerracottaPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sub-counters (Income vs Expense breakdown)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Masuk: ${formatRupiah(todayIncomeTotal)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(TerracottaPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Keluar: ${formatRupiah(todayExpenseTotal)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TerracottaDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Control Pill: Keluar / Masuk (Grounded, non-floating)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurface)
                            .border(1.dp, BorderBeige, RoundedCornerShape(12.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isExpense = activeMode == "expense"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isExpense) TerracottaPrimary else Color.Transparent)
                                .clickable { activeMode = "expense" }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Keluar (${todayExpenseList.size})",
                                fontSize = 13.sp,
                                fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                                color = if (isExpense) Color.White else StoneMuted
                            )
                        }

                        val isIncome = activeMode == "income"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isIncome) EmeraldPrimary else Color.Transparent)
                                .clickable { activeMode = "income" }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Masuk (${todayIncomeList.size})",
                                fontSize = 13.sp,
                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                                color = if (isIncome) Color.White else StoneMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeMode == "income") "CATATAN PEMASUKAN HARI INI" else "CATATAN PENGELUARAN HARI INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${displayedList.size} transaksi",
                        fontSize = 11.sp,
                        color = StoneMuted
                    )
                }
            }

            if (displayedList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (activeMode == "income")
                                "Belum ada catatan pemasukan hari ini.\nKetuk tombol + untuk mencatat."
                            else
                                "Belum ada catatan pengeluaran hari ini.\nKetuk tombol + untuk mencatat.",
                            fontSize = 14.sp,
                            color = StoneMuted,
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(displayedList, key = { it.id }) { exp ->
                    ExpenseRowCard(
                        expense = exp,
                        onClick = { onExpenseClick(exp) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ExpenseRowCard(
    expense: ExpenseEntity,
    onClick: () -> Unit
) {
    val isIncome = expense.type == "income"

    val timeString = remember(expense.timestamp) {
        expense.parsedDate()?.let {
            SimpleDateFormat("HH:mm", Locale("id", "ID")).format(it)
        } ?: ""
    }

    val editedString = remember(expense.updatedAt) {
        expense.parsedUpdatedDate()?.let {
            val sdf = SimpleDateFormat("d MMM HH:mm", Locale("id", "ID"))
            "Diedit: ${sdf.format(it)}"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(
                if (isIncome) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
                else androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isIncome) {
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(EmeraldPrimary)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isIncome) EmeraldLight else TerracottaLight),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                name = expense.categoryIcon,
                tint = if (isIncome) EmeraldPrimary else TerracottaPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.categoryName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalInk
            )
            if (expense.note.isNotBlank()) {
                Text(
                    text = expense.note,
                    fontSize = 12.sp,
                    color = StoneMuted,
                    maxLines = 1
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isIncome) "+ ${formatRupiah(expense.amount)}" else formatRupiah(expense.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) EmeraldPrimary else CharcoalInk
            )
            if (timeString.isNotBlank()) {
                Text(
                    text = timeString,
                    fontSize = 11.sp,
                    color = StoneMuted
                )
            }
            if (editedString != null) {
                Text(
                    text = editedString,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isIncome) EmeraldPrimary else TerracottaPrimary
                )
            }
        }
    }
}
