package com.simplenote.app.ui.screens.history

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.ui.components.AppIcon
import com.simplenote.app.ui.components.formatRupiah
import com.simplenote.app.ui.screens.today.ExpenseRowCard
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    expenses: List<ExpenseEntity>,
    categories: List<CategoryEntity>,
    onExpenseClick: (ExpenseEntity) -> Unit,
    onAddExpenseForDate: (Date) -> Unit = {}
) {
    var historyMonth by remember { mutableStateOf(Date()) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<CategoryEntity?>(null) }

    val cal = Calendar.getInstance().apply { time = historyMonth }
    val monthYearText = remember(historyMonth) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        sdf.format(historyMonth)
    }

    val todayCal = Calendar.getInstance()
    val isCurrentMonth = todayCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            todayCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
    val isFutureMonth = (cal.get(Calendar.YEAR) > todayCal.get(Calendar.YEAR)) ||
            (cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) && cal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH))
    val currentDay = todayCal.get(Calendar.DAY_OF_MONTH)

    val firstDayOffset = remember(historyMonth) {
        val c = Calendar.getInstance().apply {
            time = historyMonth
            set(Calendar.DAY_OF_MONTH, 1)
        }
        c.get(Calendar.DAY_OF_WEEK) - 1
    }

    val daysInMonth = remember(historyMonth) {
        val c = Calendar.getInstance().apply { time = historyMonth }
        c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val activeDays = remember(expenses, historyMonth) {
        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH)
        val set = mutableSetOf<Int>()
        expenses.forEach { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                if (c.get(Calendar.YEAR) == targetYear && c.get(Calendar.MONTH) == targetMonth) {
                    set.add(c.get(Calendar.DAY_OF_MONTH))
                }
            }
        }
        set
    }

    val filteredExpenses = remember(expenses, historyMonth, searchQuery, selectedCategoryFilter, selectedDay) {
        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH)

        expenses.filter { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                val inMonth = c.get(Calendar.YEAR) == targetYear && c.get(Calendar.MONTH) == targetMonth
                val matchesDay = selectedDay == null || c.get(Calendar.DAY_OF_MONTH) == selectedDay
                val matchesSearch = searchQuery.isBlank() ||
                        exp.note.contains(searchQuery, ignoreCase = true) ||
                        exp.categoryName.contains(searchQuery, ignoreCase = true)
                val matchesCat = selectedCategoryFilter == null || exp.categoryId == selectedCategoryFilter?.id

                inMonth && matchesDay && matchesSearch && matchesCat
            } else false
        }
    }

    val dayTotalSum = remember(filteredExpenses) { filteredExpenses.sumOf { it.amount } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector & Calendar Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Nav Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val c = Calendar.getInstance().apply { time = historyMonth; add(Calendar.MONTH, -1) }
                        historyMonth = c.time
                        selectedDay = null
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
                        val c = Calendar.getInstance().apply { time = historyMonth; add(Calendar.MONTH, 1) }
                        historyMonth = c.time
                        selectedDay = null
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Depan", tint = CharcoalInk)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Weekday Labels (Min Sen Sel Rab Kam Jum Sab)
                val weekdays = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdays.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Days Grid
                val totalSlots = firstDayOffset + daysInMonth
                val rowsCount = (totalSlots + 6) / 7
                for (rowIndex in 0 until rowsCount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (colIndex in 0 until 7) {
                            val slot = rowIndex * 7 + colIndex
                            val dayNum = slot - firstDayOffset + 1
                            if (dayNum in 1..daysInMonth) {
                                val isFuture = isFutureMonth || (isCurrentMonth && dayNum > currentDay)
                                val isToday = isCurrentMonth && dayNum == currentDay
                                val isSelected = selectedDay == dayNum
                                val hasExpense = activeDays.contains(dayNum)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .padding(horizontal = 2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) TerracottaPrimary else Color.Transparent
                                        )
                                        .then(
                                            if (isToday && !isSelected) {
                                                Modifier.border(1.dp, TerracottaPrimary, CircleShape)
                                            } else Modifier
                                        )
                                        .clickable(enabled = !isFuture) {
                                            selectedDay = if (selectedDay == dayNum) null else dayNum
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isFuture -> StoneMuted.copy(alpha = 0.35f)
                                                isSelected -> Color.White
                                                isToday -> TerracottaPrimary
                                                else -> CharcoalInk
                                            }
                                        )
                                        if (hasExpense) {
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else TerracottaPrimary)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Summary Bar with Quick Add Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurface)
                .border(1.dp, BorderBeige, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (selectedDay != null) {
                        "$selectedDay $monthYearText"
                    } else {
                        "Semua Pengeluaran Bulan Ini"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalInk
                )
                Text(
                    text = "Total: ${formatRupiah(dayTotalSum)} (${filteredExpenses.size} transaksi)",
                    fontSize = 11.sp,
                    color = StoneMuted
                )
            }

            if (selectedDay != null) {
                Button(
                    onClick = {
                        val targetCal = Calendar.getInstance().apply {
                            time = historyMonth
                            set(Calendar.DAY_OF_MONTH, selectedDay!!)
                            val nowCal = Calendar.getInstance()
                            set(Calendar.HOUR_OF_DAY, nowCal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, nowCal.get(Calendar.MINUTE))
                            set(Calendar.SECOND, nowCal.get(Calendar.SECOND))
                            set(Calendar.MILLISECOND, nowCal.get(Calendar.MILLISECOND))
                        }
                        onAddExpenseForDate(targetCal.time)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Catat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari catatan atau nominal...", fontSize = 13.sp, color = StoneMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Cari", tint = StoneMuted, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedBorderColor = TerracottaPrimary,
                unfocusedBorderColor = BorderBeige
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Semua" Chip
            item {
                val isAll = selectedCategoryFilter == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isAll) TerracottaPrimary else CardSurface)
                        .border(1.dp, if (isAll) TerracottaPrimary else BorderBeige, RoundedCornerShape(20.dp))
                        .clickable { selectedCategoryFilter = null }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Semua",
                        fontSize = 12.sp,
                        fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                        color = if (isAll) Color.White else CharcoalInk
                    )
                }
            }

            items(categories) { cat ->
                val isSelected = selectedCategoryFilter?.id == cat.id
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) TerracottaPrimary else CardSurface)
                        .border(1.dp, if (isSelected) TerracottaPrimary else BorderBeige, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedCategoryFilter = if (isSelected) null else cat
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = cat.icon,
                        tint = if (isSelected) Color.White else CharcoalInk,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cat.name,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else CharcoalInk
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transaction List
        if (filteredExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada catatan pengeluaran di periode ini.",
                    fontSize = 13.sp,
                    color = StoneMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredExpenses, key = { it.id }) { exp ->
                    ExpenseRowCard(
                        expense = exp,
                        onClick = { onExpenseClick(exp) }
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}
