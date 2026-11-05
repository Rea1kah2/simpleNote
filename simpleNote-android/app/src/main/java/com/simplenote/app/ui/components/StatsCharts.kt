package com.simplenote.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.data.model.ExpenseEntity
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaPrimary
import com.simplenote.app.ui.theme.WarmChartPalette
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CategoryStat(
    val name: String,
    val icon: String,
    val amount: Long,
    val color: Color
)

@Composable
fun DonutChart(
    categoryStats: List<CategoryStat>,
    totalAmount: Long,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 24.dp.toPx()

            // Background circle
            drawCircle(
                color = BorderBeige.copy(alpha = 0.5f),
                style = Stroke(width = strokeWidth)
            )

            if (totalAmount > 0 && categoryStats.isNotEmpty()) {
                var startAngle = -90f
                categoryStats.forEach { stat ->
                    val sweepAngle = (stat.amount.toFloat() / totalAmount) * 360f
                    drawArc(
                        color = stat.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TOTAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StoneMuted
            )
            Text(
                text = formatCompactRupiah(totalAmount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalInk
            )
        }
    }
}

enum class TrendMode {
    WEEKLY, DAILY
}

data class WeekData(
    val id: String,
    val label: String,
    val startDay: Int,
    val endDay: Int,
    val sum: Long,
    val count: Int
)

data class DayData(
    val day: Int,
    val dayNameShort: String,
    val sum: Long,
    val count: Int,
    val isToday: Boolean
)

@Composable
fun InteractiveTrendChart(
    monthlyExpenses: List<ExpenseEntity>,
    monthDate: Date,
    onPeriodSelected: (title: String, matchingExpenses: List<ExpenseEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(TrendMode.WEEKLY) }
    var selectedWeekId by remember { mutableStateOf("W1") }
    var selectedDay by remember { mutableStateOf(1) }

    val cal = Calendar.getInstance().apply { time = monthDate }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val monthName = SimpleDateFormat("MMM", Locale("id", "ID")).format(monthDate)

    val todayCal = Calendar.getInstance()
    val isCurrentMonth = todayCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            todayCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
    val currentDay = todayCal.get(Calendar.DAY_OF_MONTH)

    // Compute Weeks
    val weeks = remember(monthlyExpenses, daysInMonth) {
        val list = mutableListOf(
            WeekData("W1", "W1", 1, 7, 0, 0),
            WeekData("W2", "W2", 8, 14, 0, 0),
            WeekData("W3", "W3", 15, 21, 0, 0),
            WeekData("W4", "W4", 22, 28, 0, 0)
        )
        if (daysInMonth > 28) {
            list.add(WeekData("W5", "W5", 29, daysInMonth, 0, 0))
        }

        val parsedExpenses = monthlyExpenses.mapNotNull { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.DAY_OF_MONTH) to exp
            } else null
        }

        list.map { w ->
            val matching = parsedExpenses.filter { it.first in w.startDay..w.endDay }
            w.copy(
                sum = matching.sumOf { it.second.amount },
                count = matching.size
            )
        }
    }

    // Compute Days
    val days = remember(monthlyExpenses, daysInMonth) {
        val parsedExpenses = monthlyExpenses.mapNotNull { exp ->
            val d = exp.parsedDate()
            if (d != null) {
                val c = Calendar.getInstance().apply { time = d }
                c.get(Calendar.DAY_OF_MONTH) to exp
            } else null
        }

        val dayFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
        (1..daysInMonth).map { d ->
            val tempCal = Calendar.getInstance().apply {
                time = monthDate
                set(Calendar.DAY_OF_MONTH, d)
            }
            val dName = dayFormat.format(tempCal.time)
            val matching = parsedExpenses.filter { it.first == d }
            DayData(
                day = d,
                dayNameShort = dName,
                sum = matching.sumOf { it.second.amount },
                count = matching.size,
                isToday = isCurrentMonth && d == currentDay
            )
        }
    }

    // Initial Selection
    LaunchedEffect(mode, monthlyExpenses) {
        if (mode == TrendMode.WEEKLY) {
            val activeW = weeks.find { it.id == selectedWeekId } ?: weeks.firstOrNull()
            if (activeW != null) {
                val title = "Minggu ${activeW.label} (${activeW.startDay} - ${activeW.endDay} $monthName)"
                val matching = monthlyExpenses.filter { exp ->
                    val d = exp.parsedDate()
                    if (d != null) {
                        val c = Calendar.getInstance().apply { time = d }
                        c.get(Calendar.DAY_OF_MONTH) in activeW.startDay..activeW.endDay
                    } else false
                }
                onPeriodSelected(title, matching)
            }
        } else {
            val activeD = days.find { it.day == selectedDay } ?: days.firstOrNull()
            if (activeD != null) {
                val title = "${activeD.dayNameShort}, ${activeD.day} $monthName ${if (activeD.isToday) "(Hari Ini)" else ""}"
                val matching = monthlyExpenses.filter { exp ->
                    val d = exp.parsedDate()
                    if (d != null) {
                        val c = Calendar.getInstance().apply { time = d }
                        c.get(Calendar.DAY_OF_MONTH) == activeD.day
                    } else false
                }
                onPeriodSelected(title, matching)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Segmented Control: Mingguan vs Harian
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BorderBeige.copy(alpha = 0.5f))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (mode == TrendMode.WEEKLY) CardSurface else Color.Transparent)
                    .clickable { mode = TrendMode.WEEKLY },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mingguan",
                    fontSize = 13.sp,
                    fontWeight = if (mode == TrendMode.WEEKLY) FontWeight.Bold else FontWeight.Medium,
                    color = if (mode == TrendMode.WEEKLY) CharcoalInk else StoneMuted
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (mode == TrendMode.DAILY) CardSurface else Color.Transparent)
                    .clickable {
                        mode = TrendMode.DAILY
                        if (isCurrentMonth) selectedDay = currentDay
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Harian",
                    fontSize = 13.sp,
                    fontWeight = if (mode == TrendMode.DAILY) FontWeight.Bold else FontWeight.Medium,
                    color = if (mode == TrendMode.DAILY) CharcoalInk else StoneMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bars Container
        if (mode == TrendMode.WEEKLY) {
            val maxVal = maxOf(weeks.maxOfOrNull { it.sum } ?: 1L, 1L)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeks.forEach { w ->
                    val isSelected = w.id == selectedWeekId
                    val barHeightFraction = if (w.sum > 0) (w.sum.toFloat() / maxVal).coerceIn(0.08f, 1f) else 0.04f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clickable {
                                selectedWeekId = w.id
                                val title = "Minggu ${w.label} (${w.startDay} - ${w.endDay} $monthName)"
                                val matching = monthlyExpenses.filter { exp ->
                                    val d = exp.parsedDate()
                                    if (d != null) {
                                        val c = Calendar.getInstance().apply { time = d }
                                        c.get(Calendar.DAY_OF_MONTH) in w.startDay..w.endDay
                                    } else false
                                }
                                onPeriodSelected(title, matching)
                            }
                    ) {
                        Text(
                            text = if (w.sum > 0) formatCompactRupiah(w.sum) else "Rp 0",
                            fontSize = 10.sp,
                            color = if (isSelected) TerracottaPrimary else StoneMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(if (isSelected) TerracottaPrimary.copy(alpha = 0.12f) else Color.Transparent),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(barHeightFraction)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (w.sum > 0) (if (isSelected) TerracottaPrimary else TerracottaPrimary.copy(alpha = 0.75f)) else BorderBeige)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = w.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
                        Text(text = "${w.startDay}-${w.endDay}", fontSize = 10.sp, color = StoneMuted)
                    }
                }
            }
        } else {
            // Daily Mode: Horizontal Scroll
            val scrollState = rememberScrollState()
            val maxVal = maxOf(days.maxOfOrNull { it.sum } ?: 1L, 1L)

            LaunchedEffect(mode) {
                if (isCurrentMonth && currentDay > 5) {
                    val targetScroll = ((currentDay - 4) * 36 * 3)
                    scrollState.animateScrollTo(targetScroll)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { d ->
                    val isSelected = d.day == selectedDay
                    val barHeightFraction = if (d.sum > 0) (d.sum.toFloat() / maxVal).coerceIn(0.08f, 1f) else 0.04f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(32.dp)
                            .clickable {
                                selectedDay = d.day
                                val title = "${d.dayNameShort}, ${d.day} $monthName ${if (d.isToday) "(Hari Ini)" else ""}"
                                val matching = monthlyExpenses.filter { exp ->
                                    val parseD = exp.parsedDate()
                                    if (parseD != null) {
                                        val c = Calendar.getInstance().apply { time = parseD }
                                        c.get(Calendar.DAY_OF_MONTH) == d.day
                                    } else false
                                }
                                onPeriodSelected(title, matching)
                            }
                    ) {
                        Text(
                            text = if (d.sum > 0) formatCompactRupiah(d.sum) else "",
                            fontSize = 8.sp,
                            color = if (isSelected) TerracottaPrimary else StoneMuted,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (isSelected) TerracottaPrimary.copy(alpha = 0.12f) else Color.Transparent),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(barHeightFraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (d.sum > 0) (if (isSelected) TerracottaPrimary else TerracottaPrimary.copy(alpha = 0.75f)) else BorderBeige)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = d.day.toString(),
                            fontSize = 11.sp,
                            fontWeight = if (d.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (d.isToday) TerracottaPrimary else CharcoalInk
                        )
                        Text(text = d.dayNameShort, fontSize = 9.sp, color = StoneMuted)
                    }
                }
            }
        }
    }
}
