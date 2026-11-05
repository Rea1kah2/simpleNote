package com.simplenote.app.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.simplenote.app.ui.theme.BorderBeige
import com.simplenote.app.ui.theme.CardSurface
import com.simplenote.app.ui.theme.CharcoalInk
import com.simplenote.app.ui.theme.DangerRed
import com.simplenote.app.ui.theme.PaperBackground
import com.simplenote.app.ui.theme.StoneMuted
import com.simplenote.app.ui.theme.SuccessGreen
import com.simplenote.app.ui.theme.TerracottaPrimary
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsScreen(
    totalRecords: Int,
    onOpenCategoryManager: () -> Unit,
    onExportJson: suspend () -> String,
    onImportJson: (String) -> Unit,
    onSyncServer: (serverAddress: String) -> Unit,
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var serverAddress by remember { mutableStateOf("192.168.18.87:8080") }
    var showResetDialog by remember { mutableStateOf(false) }

    // Import file picker
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val jsonString = inputStream?.bufferedReader()?.use { it.readText() }
                if (jsonString != null) {
                    onImportJson(jsonString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pengaturan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalInk
            )
        }

        // Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Room SQLite Database Native",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalInk
                        )
                        Text(
                            text = "Penyimpanan persisten lokal mandiri",
                            fontSize = 12.sp,
                            color = StoneMuted
                        )
                    }
                    Text(
                        text = "$totalRecords data",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaPrimary
                    )
                }
            }
        }

        // Category Manager Option
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCategoryManager() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PaperBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kelola Kategori",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalInk
                        )
                        Text(
                            text = "Tambah atau ubah ikon 45 kategori",
                            fontSize = 12.sp,
                            color = StoneMuted
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = StoneMuted)
                }
            }
        }

        // Backup & Restore Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CADANGAN & PEMULIHAN (JSON)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Format cadangan 100% kompatibel dengan SimpleNote versi web iPhone.",
                        fontSize = 12.sp,
                        color = StoneMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val json = onExportJson()
                                    shareJsonFile(context, json)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ekspor JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { importLauncher.launch("*/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = CharcoalInk, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pulihkan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
                        }
                    }
                }
            }
        }

        // Local Wi-Fi Sync Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SINKRONISASI SERVER LAPTOP (WI-FI)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = serverAddress,
                            onValueChange = { serverAddress = it },
                            placeholder = { Text("IP:Port Laptop", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PaperBackground,
                                unfocusedContainerColor = PaperBackground,
                                focusedBorderColor = TerracottaPrimary,
                                unfocusedBorderColor = BorderBeige
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onSyncServer(serverAddress.trim()) },
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalInk),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sinkron", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Danger Zone: Reset
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ZONA BAHAYA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Menghapus semua riwayat transaksi dari penyimpanan database HP.",
                        fontSize = 12.sp,
                        color = StoneMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Bersihkan Semua Data", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Bersihkan Data?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus SELURUH catatan transaksi secara permanen?") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onResetData()
                }) {
                    Text("Hapus Permanen", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }
}

private fun shareJsonFile(context: Context, jsonContent: String) {
    try {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "simplenote_backup_${System.currentTimeMillis() / 1000}.json")
        file.writeText(jsonContent)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Cadangan SimpleNote")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Cadangan SimpleNote"))
    } catch (e: Exception) {
        // Fallback share as plain text if file provider fails
        val textIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, jsonContent)
        }
        context.startActivity(Intent.createChooser(textIntent, "Bagikan Cadangan SimpleNote"))
    }
}
