import Foundation
import SwiftUI

public final class TodayViewModel: ObservableObject {
    @Published public var isAddExpenseSheetPresented: Bool = false
    @Published public var selectedCategoryForFilter: Category? = nil
    
    public init() {}
    
    /// Menghitung total pengeluaran untuk hari ini dari sekumpulan expenses
    public func calculateDailyTotal(from expenses: [Expense]) -> Double {
        let today = Date()
        return expenses
            .filter { $0.timestamp.isSameDay(as: today) }
            .reduce(0.0) { $0 + $1.amount }
    }
    
    /// Filter pengeluaran yang terjadi hanya di hari ini, terurut dari yang terbaru
    public func filterTodayExpenses(from expenses: [Expense]) -> [Expense] {
        let today = Date()
        return expenses
            .filter { $0.timestamp.isSameDay(as: today) }
            .sorted { $0.timestamp > $1.timestamp }
    }
}
