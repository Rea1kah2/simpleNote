package com.simplenote.app.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.DangerRed
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.TerracottaPrimary
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatRupiah(amount: Long): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)
    return "Rp " + formatter.format(amount)
}

fun formatCompactRupiah(amount: Long): String {
    return when {
        amount >= 1_000_000 -> {
            val millions = amount / 1_000_000.0
            String.format(Locale("id", "ID"), "%.1fjt", millions).replace(".0jt", "jt").replace(",0jt", "jt")
        }
        amount >= 1_000 -> {
            val thousands = amount / 1000
            "${thousands}rb"
        }
        else -> formatRupiah(amount)
    }
}

@Composable
fun CustomNumericKeypad(
    currentAmountString: String,
    onAmountStringChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun triggerHaptic() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun handleKeyPress(key: String) {
        triggerHaptic()
        when (key) {
            "C" -> {
                if (currentAmountString.length > 1) {
                    onAmountStringChange(currentAmountString.dropLast(1))
                } else {
                    onAmountStringChange("0")
                }
            }
            "000" -> {
                if (currentAmountString != "0" && currentAmountString.length <= 9) {
                    onAmountStringChange(currentAmountString + "000")
                }
            }
            else -> {
                if (currentAmountString == "0") {
                    onAmountStringChange(key)
                } else if (currentAmountString.length < 12) {
                    onAmountStringChange(currentAmountString + key)
                }
            }
        }
    }

    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "000")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeys.forEach { key ->
                    KeypadButton(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = { handleKeyPress(key) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isAction = key == "C" || key == "000"
    val textColor = when (key) {
        "C" -> DangerRed
        "000" -> TerracottaPrimary
        else -> CharcoalInk
    }
    val bgColor = if (isAction) PaperBackground else CardSurface

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key,
            fontSize = if (key == "000") 20.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
