import SwiftUI
import UserNotifications

public struct SettingsView: View {
    @EnvironmentObject private var store: ExpenseStore
    @State private var showDeleteAllAlert: Bool = false
    
    // Notification AppStorage
    @AppStorage("isDailyReminderEnabled") private var isDailyReminderEnabled: Bool = false
    @AppStorage("reminderHour") private var reminderHour: Int = 21
    @AppStorage("reminderMinute") private var reminderMinute: Int = 0
    
    @State private var reminderTime: Date = {
        var comps = DateComponents()
        comps.hour = 21
        comps.minute = 0
        return Calendar.current.date(from: comps) ?? Date()
    }()
    @State private var showPermissionDeniedAlert: Bool = false
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            List {
                Section("Kustomisasi") {
                    NavigationLink {
                        CategoryManagerView()
                    } label: {
                        Label {
                            Text("Kelola Kategori")
                                .font(SNTypography.body())
                                .foregroundColor(Color.snInk)
                        } icon: {
                            Image(systemName: "tag.fill")
                                .foregroundColor(Color.snAccent)
                        }
                    }
                }
                
                // MARK: - Pengingat Harian (Sprint 2)
                Section("Pengingat Harian") {
                    Toggle(isOn: Binding(
                        get: { isDailyReminderEnabled },
                        set: { newValue in
                            handleToggleReminder(enabled: newValue)
                        }
                    )) {
                        Label {
                            Text("Aktifkan Pengingat")
                                .font(SNTypography.body())
                                .foregroundColor(Color.snInk)
                        } icon: {
                            Image(systemName: "bell.badge.fill")
                                .foregroundColor(Color.snAccent)
                        }
                    }
                    .tint(Color.snAccent)
                    
                    if isDailyReminderEnabled {
                        DatePicker(
                            "Waktu Pengingat",
                            selection: $reminderTime,
                            displayedComponents: .hourAndMinute
                        )
                        .font(SNTypography.body())
                        .foregroundColor(Color.snInk)
                        .onChange(of: reminderTime) { newTime in
                            let cal = Calendar.current
                            reminderHour = cal.component(.hour, from: newTime)
                            reminderMinute = cal.component(.minute, from: newTime)
                            NotificationManager.shared.scheduleDaily(at: reminderHour, minute: reminderMinute)
                        }
                        
                        HStack(alignment: .top, spacing: 8) {
                            Image(systemName: "sparkles")
                                .font(.system(size: 13))
                                .foregroundColor(Color.snAccent)
                                .padding(.top, 2)
                            
                            Text("Pengingat otomatis dibatalkan jika kamu sudah mencatat minimal 1 pengeluaran hari ini.")
                                .font(SNTypography.caption())
                                .foregroundColor(Color.snInkSecondary)
                        }
                    }
                }
                
                Section("Format & Satuan") {
                    HStack {
                        Label {
                            Text("Mata Uang")
                                .font(SNTypography.body())
                                .foregroundColor(Color.snInk)
                        } icon: {
                            Image(systemName: "dollarsign.circle.fill")
                                .foregroundColor(Color.snAccent)
                        }
                        
                        Spacer()
                        
                        Text("IDR (Rupiah)")
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                    }
                    
                    HStack {
                        Text("Contoh Format")
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                        Spacer()
                        Text((75000.0).toIndonesianRupiah)
                            .font(SNTypography.body())
                            .fontWeight(.medium)
                            .foregroundColor(Color.snInk)
                    }
                }
                
                Section("Privasi & Penyimpanan") {
                    HStack(alignment: .top, spacing: 12) {
                        Image(systemName: "lock.shield.fill")
                            .font(.system(size: 20))
                            .foregroundColor(Color.snAccent)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("100% Offline & Lokal")
                                .font(SNTypography.body())
                                .fontWeight(.medium)
                                .foregroundColor(Color.snInk)
                            
                            Text("Semua data pengeluaran disimpan langsung di iPhone menggunakan penyimpanan lokal aman (JSON Sandbox). Tidak ada tracking, analitik pihak ketiga, atau transmisi ke server cloud.")
                                .font(SNTypography.caption())
                                .foregroundColor(Color.snInkSecondary)
                                .lineSpacing(2)
                        }
                    }
                    .padding(.vertical, 4)
                }
                
                Section("Manajemen Data") {
                    HStack {
                        Text("Total Catatan Tersimpan")
                            .font(SNTypography.body())
                            .foregroundColor(Color.snInk)
                        Spacer()
                        Text("\(store.expenses.count) transaksi")
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                    }
                    
                    Button(role: .destructive) {
                        showDeleteAllAlert = true
                    } label: {
                        HStack {
                            Image(systemName: "trash.fill")
                            Text("Hapus Semua Data Pengeluaran")
                        }
                        .font(SNTypography.body())
                    }
                }
                
                Section("Tentang") {
                    HStack {
                        Text("Aplikasi")
                            .font(SNTypography.body())
                        Spacer()
                        Text("SimpleNote")
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                    }
                    
                    HStack {
                        Text("Versi")
                            .font(SNTypography.body())
                        Spacer()
                        Text("1.1.0 (Sprint 2)")
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                    }
                    
                    HStack {
                        Text("Aksen")
                            .font(SNTypography.body())
                        Spacer()
                        HStack(spacing: 6) {
                            Circle()
                                .fill(Color.snAccent)
                                .frame(width: 10, height: 10)
                            Text("Oranye Terracotta")
                                .font(SNTypography.caption())
                                .foregroundColor(Color.snInkSecondary)
                        }
                    }
                }
            }
            .listStyle(.insetGrouped)
            .navigationTitle("Pengaturan")
            .navigationBarTitleDisplayMode(.large)
            .alert("Hapus Seluruh Data?", isPresented: $showDeleteAllAlert) {
                Button("Batal", role: .cancel) {}
                Button("Hapus Semua", role: .destructive) {
                    store.deleteAllExpenses()
                }
            } message: {
                Text("Tindakan ini akan menghapus semua riwayat transaksi pengeluaran secara permanen dari perangkat ini. Tindakan ini tidak dapat dibatalkan.")
            }
            .alert("Izin Notifikasi Dibutuhkan", isPresented: $showPermissionDeniedAlert) {
                Button("OK", role: .cancel) {}
            } message: {
                Text("Silakan aktifkan izin notifikasi untuk SimpleNote di aplikasi Pengaturan iPhone agar pengingat harian dapat berfungsi.")
            }
            .onAppear {
                syncReminderTimeFromStored()
            }
        }
    }
    
    private func syncReminderTimeFromStored() {
        var comps = DateComponents()
        comps.hour = reminderHour
        comps.minute = reminderMinute
        if let date = Calendar.current.date(from: comps) {
            reminderTime = date
        }
    }
    
    private func handleToggleReminder(enabled: Bool) {
        if enabled {
            NotificationManager.shared.requestPermission { granted in
                if granted {
                    isDailyReminderEnabled = true
                    NotificationManager.shared.scheduleDaily(at: reminderHour, minute: reminderMinute)
                } else {
                    isDailyReminderEnabled = false
                    showPermissionDeniedAlert = true
                }
            }
        } else {
            isDailyReminderEnabled = false
            NotificationManager.shared.cancelAllReminders()
        }
    }
}
