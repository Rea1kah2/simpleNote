import Foundation

public struct Category: Identifiable, Codable, Equatable, Hashable {
    public var id: UUID
    public var name: String
    public var emoji: String
    public var isDefault: Bool
    public var sortOrder: Int
    
    public init(
        id: UUID = UUID(),
        name: String,
        emoji: String,
        isDefault: Bool = false,
        sortOrder: Int = 0
    ) {
        self.id = id
        self.name = name
        self.emoji = emoji
        self.isDefault = isDefault
        self.sortOrder = sortOrder
    }
}

// MARK: - Default Seed Categories
public extension Category {
    static var defaults: [Category] {
        [
            Category(name: "Makan & Minum", emoji: "🍜", isDefault: true, sortOrder: 0),
            Category(name: "Transportasi", emoji: "🚗", isDefault: true, sortOrder: 1),
            Category(name: "Belanja", emoji: "🛒", isDefault: true, sortOrder: 2),
            Category(name: "Hiburan", emoji: "🎮", isDefault: true, sortOrder: 3),
            Category(name: "Kesehatan", emoji: "💊", isDefault: true, sortOrder: 4),
            Category(name: "Tagihan & Pulsa", emoji: "📱", isDefault: true, sortOrder: 5),
            Category(name: "Pendidikan", emoji: "📚", isDefault: true, sortOrder: 6),
            Category(name: "Kebutuhan Rumah", emoji: "🏠", isDefault: true, sortOrder: 7),
            Category(name: "Hadiah & Amal", emoji: "🎁", isDefault: true, sortOrder: 8),
            Category(name: "Lainnya", emoji: "💡", isDefault: true, sortOrder: 9)
        ]
    }
}
