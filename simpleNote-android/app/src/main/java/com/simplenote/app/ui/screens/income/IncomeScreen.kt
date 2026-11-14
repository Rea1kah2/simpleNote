package com.simplenote.app.ui.screens.income

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.ui.components.formatRupiah
import com.simplenote.app.ui.screens.today.ExpenseRowCard
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.EmeraldDark
import com.simplenote.app.ui.theme.EmeraldLight
import com.simplenote.app.ui.theme.EmeraldPrimary
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun IncomeScreen(
    expenses: List<ExpenseEntity>,
    onAddIncomeClick: () -> Unit,
    onIncomeClick: (ExpenseEntity) -> Unit
) {
    val incomeList = remember(expenses) {
        expenses.filter { it.type == "income" }
    }

    val todayCal = Calendar.getInstance()
    val todayIncomes = remember(incomeList) {
        incomeList.filter { inc ->
            val d = inc.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        c.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
            } else false
        }
    }

    val monthlyIncomes = remember(incomeList) {
        incomeList.filter { inc ->
            val d = inc.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        c.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)
            } else false
        }
    }

    val totalIncomeToday = remember(todayIncomes) { todayIncomes.sumOf { it.amount } }
    val totalIncomeMonth = remember(monthlyIncomes) { monthlyIncomes.sumOf { it.amount } }

    val monthName = remember {
        SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())
    }
    val todayFullDate = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
    }

    Scaffold(
        containerColor = PaperBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddIncomeClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Catat Pemasukan",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Section Tag & Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RUANG PEMASUKAN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary,
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

                Spacer(modifier = Modifier.height(8.dp))

                // Overview Card Pemasukan Bulan Ini
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "TOTAL PEMASUKAN BULAN INI ($monthName)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StoneMuted,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "+ ${formatRupiah(totalIncomeMonth)}",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            letterSpacing = (-1).sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Accent Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(EmeraldPrimary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sub Counters: Hari Ini & Frekuensi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hari Ini: + ${formatRupiah(totalIncomeToday)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDark
                                )
                            }

                            Text(
                                text = "${monthlyIncomes.size} transaksi bulan ini",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = StoneMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SEMUA RIWAYAT PEMASUKAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${incomeList.size} catatan",
                        fontSize = 11.sp,
                        color = StoneMuted
                    )
                }
            }

            if (incomeList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada catatan pemasukan.\nKetuk tombol + di bawah untuk mencatat pemasukan baru.",
                                fontSize = 13.sp,
                                color = StoneMuted,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(incomeList, key = { it.id }) { inc ->
                    ExpenseRowCard(
                        expense = inc,
                        onClick = { onIncomeClick(inc) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
