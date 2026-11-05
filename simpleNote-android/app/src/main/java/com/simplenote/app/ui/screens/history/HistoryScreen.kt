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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.ui.components.AppIcon
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
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    var historyMonth by remember { mutableStateOf(Date()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<CategoryEntity?>(null) }

    val cal = Calendar.getInstance().apply { time = historyMonth }
    val monthYearText = remember(historyMonth) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        sdf.format(historyMonth)
    }

    val filteredExpenses = remember(expenses, historyMonth, searchQuery, selectedCategoryFilter) {
        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH)

        expenses.filter { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                val inMonth = c.get(Calendar.YEAR) == targetYear && c.get(Calendar.MONTH) == targetMonth
                val matchesSearch = searchQuery.isBlank() ||
                        exp.note.contains(searchQuery, ignoreCase = true) ||
                        exp.categoryName.contains(searchQuery, ignoreCase = true)
                val matchesCat = selectedCategoryFilter == null || exp.categoryId == selectedCategoryFilter?.id

                inMonth && matchesSearch && matchesCat
            } else false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector Bar
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
                val c = Calendar.getInstance().apply { time = historyMonth; add(Calendar.MONTH, -1) }
                historyMonth = c.time
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
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Depan", tint = CharcoalInk)
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
