import Foundation

public struct Expense: Identifiable, Codable, Equatable {
    public var id: UUID
    public var amount: Double
    public var note: String
    public var timestamp: Date
    public var categoryId: UUID?
    
    // Cached category display properties for fast render
    public var categoryName: String
    public var categoryEmoji: String
    
    public init(
        id: UUID = UUID(),
        amount: Double,
        note: String = "",
        timestamp: Date = .now,
        categoryId: UUID? = nil,
        categoryName: String = "Lainnya",
        categoryEmoji: String = "💡"
    ) {
        self.id = id
        self.amount = amount
        self.note = note
        self.timestamp = timestamp
        self.categoryId = categoryId
        self.categoryName = categoryName
        self.categoryEmoji = categoryEmoji
    }
}

// MARK: - Convenience Helpers
public extension Expense {
    var formattedAmount: String {
        amount.toIndonesianRupiah
    }
}
