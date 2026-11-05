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
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSaveExpense: (amount: Long, category: CategoryEntity, note: String, timestamp: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountString by remember { mutableStateOf("0") }
    var selectedCategory by remember(categories) {
        mutableStateOf(categories.firstOrNull() ?: CategoryEntity("cat_food", "Makan & Minum", "utensils"))
    }
    var note by remember { mutableStateOf("") }
    var showConfirmAddDialog by remember { mutableStateOf(false) }

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
                Text(
                    text = "Tambah Pengeluaran",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalInk
                )
                Text(
                    text = "Batal",
                    fontSize = 14.sp,
                    color = StoneMuted,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            // Big Amount Display (Hero Input)
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

            // Horizontal Category Selector Chips
            Text(
                text = "PILIH KATEGORI",
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
                            .clickable { selectedCategory = cat }
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

            // Optional Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Catatan singkat (misal: Mie Bangladesh, Kopi)", fontSize = 13.sp, color = StoneMuted) },
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

            // Custom Keypad built in
            CustomNumericKeypad(
                currentAmountString = amountString,
                onAmountStringChange = { amountString = it }
            )

            // Save Action Button
            Button(
                onClick = {
                    if (isFormValid) {
                        showConfirmAddDialog = true
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerracottaPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = BorderBeige,
                    disabledContentColor = StoneMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Simpan Pengeluaran",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showConfirmAddDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmAddDialog = false },
            title = { Text("Catat Pengeluaran?", fontWeight = FontWeight.Bold) },
            text = {
                val formattedAmt = java.text.NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                    maximumFractionDigits = 0
                }.format(amountLong).replace("Rp", "Rp ")
                Text("Catat pengeluaran $formattedAmt untuk kategori \"${selectedCategory.name}\"?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmAddDialog = false
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val isoTimestamp = sdf.format(Date())
                    onSaveExpense(amountLong, selectedCategory, note.trim(), isoTimestamp)
                }) {
                    Text("Simpan", color = TerracottaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmAddDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }
}
