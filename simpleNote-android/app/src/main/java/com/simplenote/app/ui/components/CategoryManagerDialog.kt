package com.simplenote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.DangerRed
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.TerracottaPrimary

@Composable
fun CategoryManagerDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAddCategory: (name: String, icon: String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    var newCatName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("sparkles") }
    var showIconPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PaperBackground),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kelola Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalInk
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = StoneMuted)
                    }
                }

                // Add Category Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Icon picker trigger
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PaperBackground)
                                    .border(1.dp, BorderBeige, RoundedCornerShape(10.dp))
                                    .clickable { showIconPicker = !showIconPicker },
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(name = selectedIcon, tint = TerracottaPrimary, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = newCatName,
                                onValueChange = { newCatName = it },
                                placeholder = { Text("Nama Kategori...", fontSize = 13.sp, color = StoneMuted) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CardSurface,
                                    unfocusedContainerColor = CardSurface,
                                    focusedBorderColor = TerracottaPrimary,
                                    unfocusedBorderColor = BorderBeige
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (newCatName.isNotBlank()) {
                                        onAddCategory(newCatName.trim(), selectedIcon)
                                        newCatName = ""
                                        showIconPicker = false
                                    }
                                },
                                enabled = newCatName.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Icon Grid Dropdown
                        if (showIconPicker) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("PILIH ICON (45 ICON TERSEDIA):", fontSize = 11.sp, color = StoneMuted, fontWeight = FontWeight.Bold)
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(6),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(IconLibrary.availableIconNames) { iconName ->
                                    val isSelected = iconName == selectedIcon
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) TerracottaPrimary.copy(alpha = 0.15f) else PaperBackground)
                                            .border(1.dp, if (isSelected) TerracottaPrimary else BorderBeige, RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedIcon = iconName
                                                showIconPicker = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(
                                            name = iconName,
                                            tint = if (isSelected) TerracottaPrimary else CharcoalInk,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Category List
                Text(
                    text = "DAFTAR KATEGORI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StoneMuted,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSurface)
                                .border(1.dp, BorderBeige, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(name = cat.icon, tint = TerracottaPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = cat.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = CharcoalInk,
                                modifier = Modifier.weight(1f)
                            )
                            if (cat.isDefault) {
                                Text("Bawaan", fontSize = 11.sp, color = StoneMuted)
                            } else {
                                IconButton(
                                    onClick = { onDeleteCategory(cat) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
