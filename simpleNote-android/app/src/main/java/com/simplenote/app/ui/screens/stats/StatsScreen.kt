package com.simplenote.app.ui.screens.stats

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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.simplenote.app.ui.components.CategoryStat
import com.simplenote.app.ui.components.DonutChart
import com.simplenote.app.ui.components.InteractiveTrendChart
import com.simplenote.app.ui.components.formatCompactRupiah
import com.simplenote.app.ui.components.formatRupiah
import com.simplenote.app.ui.screens.today.ExpenseRowCard
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.EmeraldDark
import com.simplenote.app.ui.theme.EmeraldPrimary
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaDark
import com.simplenote.app.ui.theme.TerracottaPrimary
import com.simplenote.app.ui.theme.WarmChartPalette
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    expenses: List<ExpenseEntity>,
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    var statsMonth by remember { mutableStateOf(Date()) }
    var statsMode by remember { mutableStateOf("expense") } // "expense" | "income"

    val cal = Calendar.getInstance().apply { time = statsMonth }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val monthYearText = remember(statsMonth) {
        SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(statsMonth)
    }

    val monthlyAll = remember(expenses, statsMonth) {
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        expenses.filter { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.YEAR) == y && c.get(Calendar.MONTH) == m
            } else false
        }
    }

    val monthlyExpenses = remember(monthlyAll) { monthlyAll.filter { it.type != "income" } }
    val monthlyIncomes = remember(monthlyAll) { monthlyAll.filter { it.type == "income" } }

    val totalExpense = remember(monthlyExpenses) { monthlyExpenses.sumOf { it.amount } }
    val totalIncome = remember(monthlyIncomes) { monthlyIncomes.sumOf { it.amount } }
    val netSaldo = remember(totalIncome, totalExpense) { totalIncome - totalExpense }

    val activeList = if (statsMode == "income") monthlyIncomes else monthlyExpenses
    val totalActive = if (statsMode == "income") totalIncome else totalExpense
    val avgDaily = remember(totalActive, daysInMonth) { if (totalActive > 0) totalActive / daysInMonth else 0L }

    val categoryStats = remember(activeList) {
        val map = mutableMapOf<String, Pair<String, Long>>()
        activeList.forEach { exp ->
            val current = map[exp.categoryName]
            val newAmount = (current?.second ?: 0L) + exp.amount
            map[exp.categoryName] = exp.categoryIcon to newAmount
        }
        map.entries.sortedByDescending { it.value.second }
            .mapIndexed { index, entry ->
                CategoryStat(
                    name = entry.key,
                    icon = entry.value.first,
                    amount = entry.value.second,
                    color = WarmChartPalette[index % WarmChartPalette.size]
                )
            }
    }

    var selectedPeriodTitle by remember { mutableStateOf("") }
    var selectedPeriodExpenses by remember { mutableStateOf<List<ExpenseEntity>>(emptyList()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Month Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurface)
                    .border(1.dp, BorderBeige, RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val c = Calendar.getInstance().apply { time = statsMonth; add(Calendar.MONTH, -1) }
                    statsMonth = c.time
                    selectedPeriodTitle = ""
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan Lalu", tint = CharcoalInk)
                }

                Text(
                    text = monthYearText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalInk
                )

                IconButton(onClick = {
                    val c = Calendar.getInstance().apply { time = statsMonth; add(Calendar.MONTH, 1) }
                    statsMonth = c.time
                    selectedPeriodTitle = ""
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Depan", tint = CharcoalInk)
                }
            }
        }

        // Ringkasan Saldo Bulan Ini (Balance Header Card)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "RINGKASAN SALDO BULAN INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pemasukan", fontSize = 13.sp, color = StoneMuted)
                        Text(
                            text = "+ ${formatRupiah(totalIncome)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pengeluaran", fontSize = 13.sp, color = StoneMuted)
                        Text(
                            text = "- ${formatRupiah(totalExpense)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerracottaDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BorderBeige)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Saldo Bersih", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
                        val netPrefix = if (netSaldo > 0) "+ " else ""
                        Text(
                            text = "$netPrefix${formatRupiah(netSaldo)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (netSaldo >= 0) EmeraldPrimary else TerracottaPrimary
                        )
                    }
                }
            }
        }

        // Mode Switcher Segment (Keluar / Masuk)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .border(1.dp, BorderBeige, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isExpense = statsMode == "expense"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isExpense) TerracottaPrimary else Color.Transparent)
                        .clickable {
                            statsMode = "expense"
                            selectedPeriodTitle = ""
                        }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pengeluaran (${monthlyExpenses.size})",
                        fontSize = 13.sp,
                        fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                        color = if (isExpense) Color.White else StoneMuted
                    )
                }

                val isIncome = statsMode == "income"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isIncome) EmeraldPrimary else Color.Transparent)
                        .clickable {
                            statsMode = "income"
                            selectedPeriodTitle = ""
                        }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pemasukan (${monthlyIncomes.size})",
                        fontSize = 13.sp,
                        fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                        color = if (isIncome) Color.White else StoneMuted
                    )
                }
            }
        }

        // Summary Detail Card with Donut Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    val modeLabel = if (statsMode == "income") "TOTAL PEMASUKAN" else "TOTAL PENGELUARAN"
                    Text(
                        text = modeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = formatRupiah(totalActive),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (statsMode == "income") EmeraldPrimary else CharcoalInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rata-rata: ${formatCompactRupiah(avgDaily)}/hari",
                        fontSize = 13.sp,
                        color = StoneMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Donut Chart
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        DonutChart(categoryStats = categoryStats, totalAmount = totalActive)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Breakdown Rows
                    if (categoryStats.isEmpty()) {
                        Text(
                            text = "Belum ada transaksi di bulan ini.",
                            fontSize = 13.sp,
                            color = StoneMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoryStats.forEach { cat ->
                                val pct = if (totalActive > 0) String.format(Locale.US, "%.1f", (cat.amount.toDouble() / totalActive) * 100) else "0"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(cat.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AppIcon(name = cat.icon, tint = cat.color, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CharcoalInk,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "$pct%",
                                        fontSize = 12.sp,
                                        color = StoneMuted,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Text(
                                        text = formatRupiah(cat.amount),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalInk
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tren Card (Adapts to Active Mode)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    val trendTitle = if (statsMode == "income") "TREN PEMASUKAN" else "TREN PENGELUARAN"
                    Text(
                        text = trendTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    InteractiveTrendChart(
                        monthlyExpenses = activeList,
                        monthDate = statsMonth,
                        onPeriodSelected = { title, matching ->
                            selectedPeriodTitle = title
                            selectedPeriodExpenses = matching
                        }
                    )
                }
            }
        }

        // Inline Breakdown Container
        if (selectedPeriodTitle.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedPeriodTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalInk
                    )
                    Text(
                        text = "${selectedPeriodExpenses.size} transaksi",
                        fontSize = 11.sp,
                        color = StoneMuted
                    )
                }
            }

            if (selectedPeriodExpenses.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada catatan pada periode ini.",
                        fontSize = 12.sp,
                        color = StoneMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(selectedPeriodExpenses, key = { it.id }) { exp ->
                    ExpenseRowCard(
                        expense = exp,
                        onClick = { onExpenseClick(exp) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
