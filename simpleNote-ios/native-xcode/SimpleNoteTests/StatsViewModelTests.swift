import XCTest
@testable import SimpleNote

final class StatsViewModelTests: XCTestCase {
    
    func testCalculateTotalAndCategoryStats() {
        let vm = StatsViewModel()
        let foodCategory = Category(name: "Makan & Minum", emoji: "🍜", isDefault: true)
        let transportCategory = Category(name: "Transportasi", emoji: "🚗", isDefault: true)
        
        let now = Date()
        let exp1 = Expense(amount: 50000, note: "Makan siang", timestamp: now, categoryId: foodCategory.id, categoryName: foodCategory.name, categoryEmoji: foodCategory.emoji)
        let exp2 = Expense(amount: 25000, note: "Kopi", timestamp: now, categoryId: foodCategory.id, categoryName: foodCategory.name, categoryEmoji: foodCategory.emoji)
        let exp3 = Expense(amount: 25000, note: "Ojol", timestamp: now, categoryId: transportCategory.id, categoryName: transportCategory.name, categoryEmoji: transportCategory.emoji)
        
        let expenses = [exp1, exp2, exp3]
        
        let total = vm.calculateTotal(from: expenses)
        XCTAssertEqual(total, 100000)
        
        let stats = vm.calculateCategoryStats(from: expenses)
        XCTAssertEqual(stats.count, 2)
        
        let foodStat = stats.first(where: { $0.name == "Makan & Minum" })
        XCTAssertNotNil(foodStat)
        XCTAssertEqual(foodStat?.amount, 75000)
        XCTAssertEqual(foodStat?.percentage, 75.0)
        
        let transportStat = stats.first(where: { $0.name == "Transportasi" })
        XCTAssertNotNil(transportStat)
        XCTAssertEqual(transportStat?.amount, 25000)
        XCTAssertEqual(transportStat?.percentage, 25.0)
    }
    
    func testEmptyExpensesCalculations() {
        let vm = StatsViewModel()
        let emptyExpenses: [Expense] = []
        
        XCTAssertEqual(vm.calculateTotal(from: emptyExpenses), 0.0)
        XCTAssertEqual(vm.calculateAverageDaily(from: emptyExpenses), 0.0)
        XCTAssertTrue(vm.calculateCategoryStats(from: emptyExpenses).isEmpty)
    }
}
