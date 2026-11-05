import Foundation
import UserNotifications

public final class NotificationManager {
    public static let shared = NotificationManager()
    
    private let reminderIdentifier = "simplenote.daily.reminder"
    
    private init() {}
    
    /// Meminta izin notifikasi kepada pengguna
    public func requestPermission(completion: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            DispatchQueue.main.async {
                if let error = error {
                    print("Error requesting notification authorization: \(error)")
                }
                completion(granted)
            }
        }
    }
    
    /// Cek status izin notifikasi saat ini
    public func checkAuthorizationStatus(completion: @escaping (UNAuthorizationStatus) -> Void) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                completion(settings.authorizationStatus)
            }
        }
    }
    
    /// Menjadwalkan pengingat harian pada jam dan menit tertentu
    public func scheduleDaily(at hour: Int, minute: Int) {
        cancelAllReminders()
        
        let content = UNMutableNotificationContent()
        content.title = "SimpleNote"
        content.body = "Sudah istirahat? Sempatkan 10 detik untuk mencatat pengeluaran hari ini ya ☕"
        content.sound = .default
        
        var dateComponents = DateComponents()
        dateComponents.hour = hour
        dateComponents.minute = minute
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: reminderIdentifier, content: content, trigger: trigger)
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error scheduling daily reminder: \(error)")
            }
        }
    }
    
    /// Membatalkan pengingat yang terjadwal
    public func cancelAllReminders() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [reminderIdentifier])
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: [reminderIdentifier])
    }
    
    /// Cek apakah hari ini sudah ada catatan. Jika ya dan jam sekarang belum lewat dari jam pengingat, batalkan notifikasi hari ini
    public func syncWithExpenses(hasExpensesToday: Bool, scheduledHour: Int, scheduledMinute: Int) {
        guard hasExpensesToday else { return }
        
        let now = Date()
        let calendar = Calendar.current
        let currentHour = calendar.component(.hour, from: now)
        let currentMinute = calendar.component(.minute, from: now)
        
        // Jika pengingat belum terkirim hari ini, hapus notifikasi hari ini lalu jadwalkan ulang untuk besok
        if currentHour < scheduledHour || (currentHour == scheduledHour && currentMinute < scheduledMinute) {
            cancelAllReminders()
            // Jadwalkan ulang agar besok tetap berbunyi
            scheduleDaily(at: scheduledHour, minute: scheduledMinute)
        }
    }
}
