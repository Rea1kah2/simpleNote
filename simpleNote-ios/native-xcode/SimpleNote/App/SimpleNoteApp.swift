import SwiftUI
import UserNotifications

public extension Notification.Name {
    static let triggerQuickAdd = Notification.Name("TriggerQuickAddExpense")
}

#if canImport(UIKit)
import UIKit

public class AppDelegate: NSObject, UIApplicationDelegate {
    public func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Daftarkan Quick Action Home Screen
        application.shortcutItems = [
            UIApplicationShortcutItem(
                type: "com.yourname.simplenote.addexpense",
                localizedTitle: "Catat Pengeluaran",
                localizedSubtitle: "Input cepat nominal hari ini",
                icon: UIApplicationShortcutIcon(systemImageName: "plus.circle.fill"),
                userInfo: nil
            )
        ]
        return true
    }
    
    public func application(
        _ application: UIApplication,
        performActionFor shortcutItem: UIApplicationShortcutItem,
        completionHandler: @escaping (Bool) -> Void
    ) {
        if shortcutItem.type == "com.yourname.simplenote.addexpense" {
            NotificationCenter.default.post(name: .triggerQuickAdd, object: nil)
            completionHandler(true)
        } else {
            completionHandler(false)
        }
    }
}
#endif

@main
public struct SimpleNoteApp: App {
    #if canImport(UIKit)
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    #endif
    
    @StateObject private var store = ExpenseStore()
    @Environment(\.scenePhase) private var scenePhase
    
    // Notification preferences
    @AppStorage("isDailyReminderEnabled") private var isDailyReminderEnabled: Bool = false
    @AppStorage("reminderHour") private var reminderHour: Int = 21
    @AppStorage("reminderMinute") private var reminderMinute: Int = 0
    
    public init() {}
    
    public var body: some Scene {
        WindowGroup {
            MainTabView()
                .environmentObject(store)
                .onAppear {
                    syncSmartReminder()
                }
                .onChange(of: store.expenses.count) { _ in
                    syncSmartReminder()
                }
                .onChange(of: scenePhase) { newPhase in
                    if newPhase == .active {
                        syncSmartReminder()
                    }
                }
        }
    }
    
    private func syncSmartReminder() {
        guard isDailyReminderEnabled else { return }
        let hasExpensesToday = store.expenses.contains { $0.timestamp.isToday }
        NotificationManager.shared.syncWithExpenses(
            hasExpensesToday: hasExpensesToday,
            scheduledHour: reminderHour,
            scheduledMinute: reminderMinute
        )
    }
}
