import Foundation
import SwiftUI

public final class HistoryViewModel: ObservableObject {
    @Published public var currentMonth: Date = Date().startOfMonth
    @Published public var selectedDate: Date? = nil
    @Published public var searchText: String = ""
    @Published public var selectedCategoryFilter: Category? = nil
    
    public init() {}
    
    /// Beralih ke bulan sebelumnya
    public func previousMonth() {
        if let prev = Calendar.current.date(byAdding: .month, value: -1, to: currentMonth) {
            currentMonth = prev.startOfMonth
            selectedDate = nil
        }
    }
    
    /// Beralih ke bulan berikutnya
    public func nextMonth() {
        if let next = Calendar.current.date(byAdding: .month, value: 1, to: currentMonth) {
            currentMonth = next.startOfMonth
            selectedDate = nil
        }
    }
    
    /// Reset semua filter
    public func resetFilters() {
        selectedDate = nil
        searchText = ""
        selectedCategoryFilter = nil
    }
    
    /// Filter pengeluaran yang jatuh pada bulan aktif
    public func filterExpensesForCurrentMonth(from expenses: [Expense]) -> [Expense] {
        let calendar = Calendar.current
        return expenses.filter {
            calendar.isDate($0.timestamp, equalTo: currentMonth, toGranularity: .month) &&
            calendar.isDate($0.timestamp, equalTo: currentMonth, toGranularity: .year)
        }
    }
    
    /// Filter pengeluaran dengan mempertimbangkan tanggal, pencarian teks, dan kategori
    public func filterExpenses(from expenses: [Expense]) -> [Expense] {
        let trimmedQuery = searchText.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        
        // Jika sedang melakukan pencarian teks, cari di seluruh riwayat agar user mudah menemukan transaksi lama
        let baseList: [Expense] = trimmedQuery.isEmpty
            ? filterExpensesForCurrentMonth(from: expenses)
            : expenses
        
        var result = baseList
        
        // Filter tanggal (hanya jika tidak sedang search teks bebas)
        if trimmedQuery.isEmpty, let selected = selectedDate {
            result = result.filter { $0.timestamp.isSameDay(as: selected) }
        }
        
        // Filter kategori jika ada yang dipilih
        if let cat = selectedCategoryFilter {
            result = result.filter { $0.categoryId == cat.id || $0.categoryName == cat.name }
        }
        
        // Filter query pencarian
        if !trimmedQuery.isEmpty {
            result = result.filter {
                $0.note.lowercased().contains(trimmedQuery) ||
                $0.categoryName.lowercased().contains(trimmedQuery) ||
                $0.formattedAmount.lowercased().contains(trimmedQuery)
            }
        }
        
        return result.sorted { $0.timestamp > $1.timestamp }
    }
    
    /// Mengelompokkan pengeluaran berdasarkan awal hari untuk tampilan timeline berurutan
    public func groupExpensesByDay(from expenses: [Expense]) -> [(Date, [Expense])] {
        let filtered = filterExpenses(from: expenses)
        let grouped = Dictionary(grouping: filtered) { $0.timestamp.startOfDay }
        return grouped
            .map { ($0.key, $0.value.sorted { $0.timestamp > $1.timestamp }) }
            .sorted { $0.0 > $1.0 }
    }
    
    /// Kumpulan tanggal dalam bulan aktif yang memiliki minimal 1 pengeluaran (untuk indikator titik kalender)
    public func activeSpendingDays(from expenses: [Expense]) -> Set<Date> {
        let monthly = filterExpensesForCurrentMonth(from: expenses)
        let days = monthly.map { $0.timestamp.startOfDay }
        return Set(days)
    }
    
    /// Menghitung total pengeluaran untuk hasil filter saat ini
    public func totalForCurrentFilter(from expenses: [Expense]) -> Double {
        filterExpenses(from: expenses).reduce(0.0) { $0 + $1.amount }
    }
    
    /// Menghitung total pengeluaran untuk bulan yang sedang ditampilkan
    public func totalForMonth(from expenses: [Expense]) -> Double {
        filterExpensesForCurrentMonth(from: expenses).reduce(0.0) { $0 + $1.amount }
    }
}
