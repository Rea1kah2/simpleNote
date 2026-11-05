import Foundation
import SwiftUI

public final class ExpenseStore: ObservableObject {
    @Published public var expenses: [Expense] = []
    @Published public var categories: [Category] = []
    
    private let expensesFileName = "simplenote_expenses.json"
    private let categoriesFileName = "simplenote_categories.json"
    
    public init() {
        loadCategories()
        loadExpenses()
    }
    
    // MARK: - File Paths
    private var documentsDirectory: URL {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    }
    
    private var expensesFileURL: URL {
        documentsDirectory.appendingPathComponent(expensesFileName)
    }
    
    private var categoriesFileURL: URL {
        documentsDirectory.appendingPathComponent(categoriesFileName)
    }
    
    // MARK: - Category Management
    public func loadCategories() {
        if FileManager.default.fileExists(atPath: categoriesFileURL.path) {
            do {
                let data = try Data(contentsOf: categoriesFileURL)
                let decoded = try JSONDecoder().decode([Category].self, from: data)
                self.categories = decoded.sorted { $0.sortOrder < $1.sortOrder }
            } catch {
                print("Error loading categories: \(error). Falling back to defaults.")
                self.categories = Category.defaults
                saveCategories()
            }
        } else {
            self.categories = Category.defaults
            saveCategories()
        }
    }
    
    public func saveCategories() {
        do {
            let data = try JSONEncoder().encode(categories)
            try data.write(to: categoriesFileURL, options: .atomic)
        } catch {
            print("Error saving categories: \(error)")
        }
    }
    
    public func addCategory(name: String, emoji: String) {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        let nextOrder = (categories.map(\.sortOrder).max() ?? 0) + 1
        let newCat = Category(name: trimmed, emoji: emoji, isDefault: false, sortOrder: nextOrder)
        categories.append(newCat)
        saveCategories()
    }
    
    public func deleteCategory(_ category: Category) {
        guard !category.isDefault else { return }
        categories.removeAll { $0.id == category.id }
        saveCategories()
        
        // Update expenses that used this category to "Lainnya"
        for i in 0..<expenses.count {
            if expenses[i].categoryId == category.id {
                expenses[i].categoryId = nil
                expenses[i].categoryName = "Lainnya"
                expenses[i].categoryEmoji = "💡"
            }
        }
        saveExpenses()
    }
    
    // MARK: - Expense Management
    public func loadExpenses() {
        if FileManager.default.fileExists(atPath: expensesFileURL.path) {
            do {
                let data = try Data(contentsOf: expensesFileURL)
                let decoded = try JSONDecoder().decode([Expense].self, from: data)
                self.expenses = decoded.sorted { $0.timestamp > $1.timestamp }
            } catch {
                print("Error loading expenses: \(error)")
                self.expenses = []
            }
        } else {
            self.expenses = []
        }
    }
    
    public func saveExpenses() {
        do {
            let data = try JSONEncoder().encode(expenses)
            try data.write(to: expensesFileURL, options: .atomic)
        } catch {
            print("Error saving expenses: \(error)")
        }
    }
    
    public func addExpense(amount: Double, category: Category?, note: String, timestamp: Date = .now) {
        guard amount > 0 else { return }
        let newExpense = Expense(
            amount: amount,
            note: note.trimmingCharacters(in: .whitespacesAndNewlines),
            timestamp: timestamp,
            categoryId: category?.id,
            categoryName: category?.name ?? "Lainnya",
            categoryEmoji: category?.emoji ?? "💡"
        )
        expenses.insert(newExpense, at: 0)
        saveExpenses()
        
        #if canImport(UIKit)
        let feedback = UINotificationFeedbackGenerator()
        feedback.notificationOccurred(.success)
        #endif
    }
    
    public func updateExpense(_ updated: Expense) {
        if let idx = expenses.firstIndex(where: { $0.id == updated.id }) {
            expenses[idx] = updated
            // Keep expenses sorted descending by timestamp
            expenses.sort { $0.timestamp > $1.timestamp }
            saveExpenses()
            
            #if canImport(UIKit)
            let feedback = UINotificationFeedbackGenerator()
            feedback.notificationOccurred(.success)
            #endif
        }
    }
    
    public func deleteExpense(_ expense: Expense) {
        expenses.removeAll { $0.id == expense.id }
        saveExpenses()
        
        #if canImport(UIKit)
        let feedback = UIImpactFeedbackGenerator(style: .medium)
        feedback.impactOccurred()
        #endif
    }
    
    public func deleteAllExpenses() {
        expenses.removeAll()
        saveExpenses()
    }
}
