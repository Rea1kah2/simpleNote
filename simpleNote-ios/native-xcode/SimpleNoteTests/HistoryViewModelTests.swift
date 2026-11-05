import XCTest
@testable import SimpleNote

final class HistoryViewModelTests: XCTestCase {
    
    func testSearchAndFilterExpenses() {
        let vm = HistoryViewModel()
        let foodCategory = Category(name: "Makan & Minum", emoji: "🍜", isDefault: true)
        let transportCategory = Category(name: "Transportasi", emoji: "🚗", isDefault: true)
        
        let now = Date()
        let exp1 = Expense(amount: 45000, note: "Ayam geprek pedas", timestamp: now, categoryId: foodCategory.id, categoryName: foodCategory.name, categoryEmoji: foodCategory.emoji)
        let exp2 = Expense(amount: 20000, note: "Es kopi susu", timestamp: now, categoryId: foodCategory.id, categoryName: foodCategory.name, categoryEmoji: foodCategory.emoji)
        let exp3 = Expense(amount: 15000, note: "Ojol kantor", timestamp: now, categoryId: transportCategory.id, categoryName: transportCategory.name, categoryEmoji: transportCategory.emoji)
        
        let expenses = [exp1, exp2, exp3]
        
        // 1. Text search by note keyword
        vm.searchText = "geprek"
        let filtered1 = vm.filterExpenses(from: expenses)
        XCTAssertEqual(filtered1.count, 1)
        XCTAssertEqual(filtered1.first?.note, "Ayam geprek pedas")
        
        // 2. Text search by category keyword
        vm.searchText = "transportasi"
        let filtered2 = vm.filterExpenses(from: expenses)
        XCTAssertEqual(filtered2.count, 1)
        XCTAssertEqual(filtered2.first?.categoryName, "Transportasi")
        
        // 3. Category chip filter
        vm.searchText = ""
        vm.selectedCategoryFilter = foodCategory
        let filtered3 = vm.filterExpenses(from: expenses)
        XCTAssertEqual(filtered3.count, 2)
        
        // 4. Combined filter with no matches
        vm.searchText = "buku tulis"
        let filtered4 = vm.filterExpenses(from: expenses)
        XCTAssertEqual(filtered4.count, 0)
    }
}
