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
import androidx.compose.runtime.remember
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
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
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
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    val todayExpenses = remember(expenses) {
        val todayCal = Calendar.getInstance()
        expenses.filter { exp ->
            try {
                val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(exp.timestamp.take(19))
                if (d != null) {
                    val c = Calendar.getInstance().apply { time = d }
                    c.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                            c.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
                } else false
            } catch (e: Exception) { false }
        }
    }

    val todayTotal = remember(todayExpenses) {
        todayExpenses.sumOf { it.amount }
    }

    val todayFullDate = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        sdf.format(Date())
    }

    Scaffold(
        containerColor = PaperBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = TerracottaPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Pengeluaran",
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

                // Canvas Hero Header (52pt)
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatRupiah(todayTotal),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalInk,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Terracotta Signature Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TerracottaPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CATATAN HARI INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${todayExpenses.size} transaksi",
                        fontSize = 11.sp,
                        color = StoneMuted
                    )
                }
            }

            if (todayExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada catatan hari ini.\nKetuk tombol + untuk mencatat.",
                            fontSize = 14.sp,
                            color = StoneMuted,
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(todayExpenses, key = { it.id }) { exp ->
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
    val timeString = remember(expense.timestamp) {
        try {
            val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(expense.timestamp.take(19))
            if (d != null) SimpleDateFormat("HH:mm", Locale("id", "ID")).format(d) else ""
        } catch (e: Exception) { "" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(1.dp, BorderBeige, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TerracottaLight),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                name = expense.categoryIcon,
                tint = TerracottaPrimary,
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
                text = formatRupiah(expense.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalInk
            )
            if (timeString.isNotBlank()) {
                Text(
                    text = timeString,
                    fontSize = 11.sp,
                    color = StoneMuted
                )
            }
        }
    }
}
