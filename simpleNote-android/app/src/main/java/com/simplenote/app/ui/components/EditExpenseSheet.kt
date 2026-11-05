package com.simplenote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.DangerRed
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseSheet(
    expense: ExpenseEntity,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSaveEdit: (updatedExpense: ExpenseEntity) -> Unit,
    onDeleteExpense: (expense: ExpenseEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountString by remember { mutableStateOf(expense.amount.toString()) }
    var selectedCategory by remember(categories) {
        mutableStateOf(
            categories.find { it.id == expense.categoryId }
                ?: CategoryEntity(expense.categoryId, expense.categoryName, expense.categoryIcon)
        )
    }
    var selectedIcon by remember { mutableStateOf(expense.categoryIcon) }
    var note by remember { mutableStateOf(expense.note) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showConfirmSaveDialog by remember { mutableStateOf(false) }

    val createdTimeString = remember(expense.timestamp) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val d = parser.parse(expense.timestamp.take(19))
            if (d != null) SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID")).format(d) else ""
        } catch (e: Exception) { "" }
    }

    val updatedTimeString = remember(expense.updatedAt) {
        expense.updatedAt?.let {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val d = parser.parse(it.take(19))
                if (d != null) SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID")).format(d) else null
            } catch (e: Exception) { null }
        }
    }

    val amountLong = amountString.toLongOrNull() ?: 0L
    val isFormValid = amountLong > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PaperBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderBeige)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ubah Catatan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalInk
                    )
                    if (createdTimeString.isNotBlank()) {
                        Text(
                            text = "Dibuat: $createdTimeString",
                            fontSize = 11.sp,
                            color = StoneMuted
                        )
                    }
                    if (updatedTimeString != null) {
                        Text(
                            text = "Diedit: $updatedTimeString",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TerracottaPrimary
                        )
                    }
                }
                Text(
                    text = "Batal",
                    fontSize = 14.sp,
                    color = StoneMuted,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            // Big Amount Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, BorderBeige, RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatRupiah(amountLong),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (amountLong > 0) TerracottaPrimary else StoneMuted,
                    textAlign = TextAlign.Center
                )
            }

            // Category Chips
            Text(
                text = "KATEGORI",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StoneMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.width(12.dp)) }
                items(categories) { cat ->
                    val isSelected = cat.id == selectedCategory.id
                    val chipBg = if (isSelected) TerracottaPrimary else CardSurface
                    val chipContentColor = if (isSelected) Color.White else CharcoalInk

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .border(1.dp, if (isSelected) TerracottaPrimary else BorderBeige, RoundedCornerShape(20.dp))
                            .clickable {
                                selectedCategory = cat
                                selectedIcon = cat.icon
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            name = cat.icon,
                            tint = chipContentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.name,
                            color = chipContentColor,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                item { Spacer(modifier = Modifier.width(12.dp)) }
            }

            // Note Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Catatan singkat...", fontSize = 13.sp, color = StoneMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface,
                    focusedBorderColor = TerracottaPrimary,
                    unfocusedBorderColor = BorderBeige
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Custom Keypad
            CustomNumericKeypad(
                currentAmountString = amountString,
                onAmountStringChange = { amountString = it }
            )

            // Buttons: Simpan & Hapus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showConfirmDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DangerRed)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (isFormValid) {
                            showConfirmSaveDialog = true
                        }
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(50.dp)
                ) {
                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Dialog Konfirmasi Simpan Perubahan
    if (showConfirmSaveDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmSaveDialog = false },
            title = { Text("Simpan Perubahan?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menyimpan perubahan pada transaksi ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmSaveDialog = false
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val nowIso = sdf.format(Date())
                    val updated = expense.copy(
                        amount = amountLong,
                        categoryId = selectedCategory.id,
                        categoryName = selectedCategory.name,
                        categoryIcon = selectedIcon,
                        note = note.trim(),
                        updatedAt = nowIso
                    )
                    onSaveEdit(updated)
                }) {
                    Text("Simpan", color = TerracottaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSaveDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }

    // Dialog Konfirmasi Hapus Transaksi
    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = { Text("Hapus Catatan?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus catatan transaksi ini secara permanen?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDeleteDialog = false
                    onDeleteExpense(expense)
                }) {
                    Text("Hapus", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }
}
