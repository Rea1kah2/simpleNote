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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    cloudUrl: String,
    isAutoSyncEnabled: Boolean,
    lastSyncStatus: String,
    hasDeletePassword: Boolean,
    onSaveCloudUrl: (String) -> Unit,
    onToggleAutoSync: (Boolean) -> Unit,
    onTestConnection: (String) -> Unit,
    onOpenCategoryManager: () -> Unit,
    onExportJson: suspend () -> String,
    onImportJson: (String) -> Unit,
    onSyncServer: (serverAddress: String) -> Unit,
    onSetDeletePassword: (String) -> Unit,
    onVerifyDeletePassword: (String) -> Boolean,
    onChangeDeletePassword: (String, String) -> Boolean,
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var serverAddress by remember(cloudUrl) { mutableStateOf(cloudUrl) }
    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showVerifyPasswordDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showFinalConfirmResetDialog by remember { mutableStateOf(false) }

    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var verifyPasswordInput by remember { mutableStateOf("") }
    var oldPasswordInput by remember { mutableStateOf("") }
    var changeNewPassInput by remember { mutableStateOf("") }
    var changeConfirmPassInput by remember { mutableStateOf("") }
    var passwordErrorMsg by remember { mutableStateOf<String?>(null) }

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

        // Cloud & Server 24/7 Sync Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SINKRONISASI CLOUD & SERVER (24/7)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StoneMuted,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (lastSyncStatus.isNotBlank()) {
                            Text(
                                text = lastSyncStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = SuccessGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sinkronisasi otomatis dua arah dengan Vercel Cloud atau server lokal.",
                        fontSize = 12.sp,
                        color = StoneMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = serverAddress,
                        onValueChange = {
                            serverAddress = it
                            onSaveCloudUrl(it)
                        },
                        placeholder = { Text("https://simplenote-kamu.vercel.app", fontSize = 12.sp, color = StoneMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PaperBackground,
                            unfocusedContainerColor = PaperBackground,
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveCloudUrl(serverAddress.trim())
                                onTestConnection(serverAddress.trim())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PaperBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = CharcoalInk, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tes Koneksi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CharcoalInk)
                        }

                        Button(
                            onClick = {
                                onSaveCloudUrl(serverAddress.trim())
                                onSyncServer(serverAddress.trim())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalInk),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sinkronkan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-sync switch row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PaperBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Sync Latar Belakang",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalInk
                            )
                            Text(
                                text = "Otomatis sinkron saat aplikasi dibuka",
                                fontSize = 11.sp,
                                color = StoneMuted
                            )
                        }
                        Switch(
                            checked = isAutoSyncEnabled,
                            onCheckedChange = { onToggleAutoSync(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TerracottaPrimary,
                                uncheckedThumbColor = StoneMuted,
                                uncheckedTrackColor = BorderBeige
                            )
                        )
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                passwordErrorMsg = null
                                if (!hasDeletePassword) {
                                    newPasswordInput = ""
                                    confirmPasswordInput = ""
                                    showSetPasswordDialog = true
                                } else {
                                    verifyPasswordInput = ""
                                    showVerifyPasswordDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bersihkan Semua Data", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (hasDeletePassword) {
                            Button(
                                onClick = {
                                    oldPasswordInput = ""
                                    changeNewPassInput = ""
                                    changeConfirmPassInput = ""
                                    passwordErrorMsg = null
                                    showChangePasswordDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderBeige),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = CharcoalInk, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ubah Sandi", color = CharcoalInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Dialog 1: Buat Password Baru (Pertama kali)
    if (showSetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showSetPasswordDialog = false },
            title = { Text("Buat Sandi Rahasia", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Untuk keamanan, tentukan sandi atau pesan rahasia yang hanya kamu ketahui sebelum menghapus data.",
                        fontSize = 13.sp,
                        color = StoneMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = {
                            newPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Sandi Rahasia Baru") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = {
                            confirmPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Ulangi Sandi") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordErrorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passwordErrorMsg ?: "", color = DangerRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPasswordInput.isBlank()) {
                        passwordErrorMsg = "Sandi tidak boleh kosong"
                    } else if (newPasswordInput != confirmPasswordInput) {
                        passwordErrorMsg = "Konfirmasi sandi tidak cocok"
                    } else {
                        onSetDeletePassword(newPasswordInput)
                        showSetPasswordDialog = false
                        showFinalConfirmResetDialog = true
                    }
                }) {
                    Text("Simpan & Lanjut", color = TerracottaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetPasswordDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }

    // Dialog 2: Masukkan Password untuk Verifikasi
    if (showVerifyPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyPasswordDialog = false },
            title = { Text("Autentikasi Sandi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Masukkan sandi rahasia untuk memverifikasi penghapusan seluruh data transaksi.",
                        fontSize = 13.sp,
                        color = StoneMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = verifyPasswordInput,
                        onValueChange = {
                            verifyPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Sandi Rahasia") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordErrorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passwordErrorMsg ?: "", color = DangerRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (onVerifyDeletePassword(verifyPasswordInput)) {
                        showVerifyPasswordDialog = false
                        showFinalConfirmResetDialog = true
                    } else {
                        passwordErrorMsg = "Sandi salah! Periksa kembali."
                    }
                }) {
                    Text("Verifikasi", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerifyPasswordDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }

    // Dialog 3: Ubah Sandi Rahasia
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Ubah Sandi Rahasia", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = oldPasswordInput,
                        onValueChange = {
                            oldPasswordInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Sandi Lama") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = changeNewPassInput,
                        onValueChange = {
                            changeNewPassInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Sandi Baru") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = changeConfirmPassInput,
                        onValueChange = {
                            changeConfirmPassInput = it
                            passwordErrorMsg = null
                        },
                        label = { Text("Ulangi Sandi Baru") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = BorderBeige
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordErrorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passwordErrorMsg ?: "", color = DangerRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (changeNewPassInput.isBlank()) {
                        passwordErrorMsg = "Sandi baru tidak boleh kosong"
                    } else if (changeNewPassInput != changeConfirmPassInput) {
                        passwordErrorMsg = "Konfirmasi sandi baru tidak cocok"
                    } else {
                        val success = onChangeDeletePassword(oldPasswordInput, changeNewPassInput)
                        if (success) {
                            showChangePasswordDialog = false
                        } else {
                            passwordErrorMsg = "Sandi lama tidak sesuai"
                        }
                    }
                }) {
                    Text("Simpan", color = TerracottaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Batal", color = CharcoalInk)
                }
            }
        )
    }

    // Dialog 4: Konfirmasi Akhir Hapus Semua Data
    if (showFinalConfirmResetDialog) {
        AlertDialog(
            onDismissRequest = { showFinalConfirmResetDialog = false },
            title = { Text("Hapus Semua Data?", fontWeight = FontWeight.Bold) },
            text = { Text("Sandi terverifikasi. Apakah kamu yakin ingin menghapus SELURUH catatan transaksi secara permanen?") },
            confirmButton = {
                TextButton(onClick = {
                    showFinalConfirmResetDialog = false
                    onResetData()
                }) {
                    Text("Hapus Permanen", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalConfirmResetDialog = false }) {
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
