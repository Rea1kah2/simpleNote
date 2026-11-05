import Foundation
import SwiftUI

public struct CategoryStat: Identifiable {
    public var id: String { name }
    public let name: String
    public let emoji: String
    public let amount: Double
    public let percentage: Double
    public let color: Color
}

public struct DailySpendStat: Identifiable {
    public var id: Int { dayNumber }
    public let dayNumber: Int
    public let date: Date
    public let amount: Double
}

public final class StatsViewModel: ObservableObject {
    @Published public var selectedMonth: Date = Date().startOfMonth
    
    public init() {}
    
    public func previousMonth() {
        if let prev = Calendar.current.date(byAdding: .month, value: -1, to: selectedMonth) {
            selectedMonth = prev.startOfMonth
        }
    }
    
    public func nextMonth() {
        if let next = Calendar.current.date(byAdding: .month, value: 1, to: selectedMonth) {
            selectedMonth = next.startOfMonth
        }
    }
    
    public func filterExpenses(from expenses: [Expense]) -> [Expense] {
        let calendar = Calendar.current
        return expenses.filter {
            calendar.isDate($0.timestamp, equalTo: selectedMonth, toGranularity: .month) &&
            calendar.isDate($0.timestamp, equalTo: selectedMonth, toGranularity: .year)
        }
    }
    
    public func calculateTotal(from expenses: [Expense]) -> Double {
        filterExpenses(from: expenses).reduce(0.0) { $0 + $1.amount }
    }
    
    public func calculateAverageDaily(from expenses: [Expense]) -> Double {
        let monthly = filterExpenses(from: expenses)
        let total = monthly.reduce(0.0) { $0 + $1.amount }
        let calendar = Calendar.current
        let daysInMonth = calendar.range(of: .day, in: .month, for: selectedMonth)?.count ?? 30
        return total / Double(daysInMonth)
    }
    
    public func calculateCategoryStats(from expenses: [Expense]) -> [CategoryStat] {
        let monthly = filterExpenses(from: expenses)
        let total = monthly.reduce(0.0) { $0 + $1.amount }
        guard total > 0 else { return [] }
        
        let grouped = Dictionary(grouping: monthly) { $0.categoryName }
        
        let palette: [Color] = [
            Color.snAccent,
            Color(hex: "D97706"), // Amber Gold
            Color(hex: "EA580C"), // Burnt Sienna
            Color(hex: "0F766E"), // Deep Teal
            Color(hex: "4D7C0F"), // Warm Olive
            Color(hex: "BE185D"), // Dusty Rose
            Color(hex: "475569"), // Slate Ink
            Color(hex: "B45309"), // Warm Ochre
            Color(hex: "E11D48"), // Crimson Rose
            Color(hex: "78716C")  // Warm Stone
        ]
        
        var stats: [CategoryStat] = []
        var colorIndex = 0
        
        for (name, list) in grouped {
            let catAmount = list.reduce(0.0) { $0 + $1.amount }
            let emoji = list.first?.categoryEmoji ?? "💡"
            let percentage = (catAmount / total) * 100
            let color = palette[colorIndex % palette.count]
            colorIndex += 1
            
            stats.append(
                CategoryStat(
                    name: name,
                    emoji: emoji,
                    amount: catAmount,
                    percentage: percentage,
                    color: color
                )
            )
        }
        
        return stats.sorted { $0.amount > $1.amount }
    }
    
    public func calculateDailyTrend(from expenses: [Expense]) -> [DailySpendStat] {
        let monthly = filterExpenses(from: expenses)
        let calendar = Calendar.current
        let totalDays = calendar.range(of: .day, in: .month, for: selectedMonth)?.count ?? 30
        
        var dailyMap: [Int: Double] = [:]
        for exp in monthly {
            let day = calendar.component(.day, from: exp.timestamp)
            dailyMap[day, default: 0.0] += exp.amount
        }
        
        var results: [DailySpendStat] = []
        for day in 1...totalDays {
            var dateComponents = calendar.dateComponents([.year, .month], from: selectedMonth)
            dateComponents.day = day
            let date = calendar.date(from: dateComponents) ?? selectedMonth
            let amount = dailyMap[day] ?? 0.0
            results.append(DailySpendStat(dayNumber: day, date: date, amount: amount))
        }
        
        return results
    }
}
