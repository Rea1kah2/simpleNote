import SwiftUI

public struct MainTabView: View {
    @EnvironmentObject private var store: ExpenseStore
    @State private var selectedTab: Int = 0
    @State private var isQuickAddSheetPresented: Bool = false
    
    public init() {}
    
    public var body: some View {
        TabView(selection: $selectedTab) {
            TodayView()
                .tabItem {
                    Label("Hari Ini", systemImage: "sun.max.fill")
                }
                .tag(0)
            
            HistoryView()
                .tabItem {
                    Label("Riwayat", systemImage: "calendar")
                }
                .tag(1)
            
            StatsView()
                .tabItem {
                    Label("Statistik", systemImage: "chart.bar.xaxis")
                }
                .tag(2)
            
            SettingsView()
                .tabItem {
                    Label("Pengaturan", systemImage: "slider.horizontal.3")
                }
                .tag(3)
        }
        .tint(Color.snAccent)
        .onReceive(NotificationCenter.default.publisher(for: .triggerQuickAdd)) { _ in
            selectedTab = 0
            isQuickAddSheetPresented = true
        }
        .sheet(isPresented: $isQuickAddSheetPresented) {
            AddExpenseSheet { amount, category, note, timestamp in
                store.addExpense(
                    amount: amount,
                    category: category,
                    note: note,
                    timestamp: timestamp
                )
            }
            .presentationDetents([.fraction(0.88), .large])
            .presentationDragIndicator(.visible)
        }
    }
}
