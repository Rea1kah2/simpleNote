import SwiftUI

public struct HistoryView: View {
    @EnvironmentObject private var store: ExpenseStore
    @StateObject private var viewModel = HistoryViewModel()
    @State private var editingExpense: Expense? = nil
    
    private var groupedExpenses: [(Date, [Expense])] {
        viewModel.groupExpensesByDay(from: store.expenses)
    }
    
    private var filteredTotal: Double {
        viewModel.totalForCurrentFilter(from: store.expenses)
    }
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            ZStack {
                Color.snCanvas
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 14) {
                        // Interactive Calendar Grid (sembunyikan saat user aktif mencari teks agar tidak sesak)
                        if viewModel.searchText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                            CalendarGridComponent(
                                month: viewModel.currentMonth,
                                selectedDate: $viewModel.selectedDate,
                                activeDays: viewModel.activeSpendingDays(from: store.expenses),
                                onPreviousMonth: { viewModel.previousMonth() },
                                onNextMonth: { viewModel.nextMonth() }
                            )
                            .padding(.horizontal, 16)
                            .padding(.top, 4)
                        }
                        
                        // Category Filter Chips (Horizontal)
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                // "Semua" chip
                                Button(action: {
                                    viewModel.selectedCategoryFilter = nil
                                }) {
                                    Text("Semua")
                                        .font(SNTypography.caption())
                                        .fontWeight(.medium)
                                        .foregroundColor(viewModel.selectedCategoryFilter == nil ? .white : Color.snInk)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(
                                            Capsule()
                                                .fill(viewModel.selectedCategoryFilter == nil ? Color.snAccent : Color.snSurface)
                                        )
                                        .overlay(
                                            Capsule()
                                                .stroke(viewModel.selectedCategoryFilter == nil ? Color.clear : Color.snBorder, lineWidth: 0.8)
                                        )
                                }
                                .buttonStyle(.plain)
                                
                                ForEach(store.categories) { category in
                                    let isSelected = viewModel.selectedCategoryFilter?.id == category.id
                                    Button(action: {
                                        if isSelected {
                                            viewModel.selectedCategoryFilter = nil
                                        } else {
                                            viewModel.selectedCategoryFilter = category
                                        }
                                    }) {
                                        HStack(spacing: 4) {
                                            Text(category.emoji)
                                                .font(.system(size: 13))
                                            Text(category.name)
                                                .font(SNTypography.caption())
                                                .fontWeight(.medium)
                                        }
                                        .foregroundColor(isSelected ? .white : Color.snInk)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(
                                            Capsule()
                                                .fill(isSelected ? Color.snAccent : Color.snSurface)
                                        )
                                        .overlay(
                                            Capsule()
                                                .stroke(isSelected ? Color.clear : Color.snBorder, lineWidth: 0.8)
                                        )
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                            .padding(.horizontal, 16)
                        }
                        .padding(.vertical, 2)
                        
                        // Filter & Total Info Pill
                        HStack {
                            if !viewModel.searchText.isEmpty {
                                HStack(spacing: 6) {
                                    Text("Pencarian: \"\(viewModel.searchText)\"")
                                        .font(SNTypography.caption())
                                        .foregroundColor(Color.snAccent)
                                        .fontWeight(.medium)
                                    
                                    Button(action: { viewModel.searchText = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .font(.system(size: 14))
                                            .foregroundColor(Color.snAccent)
                                    }
                                }
                                .padding(.horizontal, 10)
                                .padding(.vertical, 4)
                                .background(Capsule().fill(Color.snAccentSoft))
                            } else if let selected = viewModel.selectedDate {
                                HStack(spacing: 6) {
                                    Text(selected.relativeDayString)
                                        .font(SNTypography.caption())
                                        .foregroundColor(Color.snAccent)
                                        .fontWeight(.medium)
                                    
                                    Button(action: { viewModel.selectedDate = nil }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .font(.system(size: 14))
                                            .foregroundColor(Color.snAccent)
                                    }
                                }
                                .padding(.horizontal, 10)
                                .padding(.vertical, 4)
                                .background(Capsule().fill(Color.snAccentSoft))
                            } else {
                                Text("Riwayat Bulan Ini")
                                    .font(SNTypography.caption())
                                    .foregroundColor(Color.snInkSecondary)
                            }
                            
                            Spacer()
                            
                            Text("Total: \(filteredTotal.toIndonesianRupiah)")
                                .font(SNTypography.amountRow())
                                .foregroundColor(Color.snInk)
                        }
                        .padding(.horizontal, 20)
                        
                        // Transaction List Grouped by Day
                        if groupedExpenses.isEmpty {
                            EmptyStateView(
                                title: "Tidak Ada Catatan",
                                message: !viewModel.searchText.isEmpty
                                    ? "Tidak ada transaksi yang cocok dengan \"\(viewModel.searchText)\"."
                                    : (viewModel.selectedDate != nil ? "Tidak ada pengeluaran pada tanggal ini." : "Belum ada pengeluaran di filter ini."),
                                systemImage: "magnifyingglass"
                            )
                            .padding(.top, 24)
                        } else {
                            LazyVStack(alignment: .leading, spacing: 18) {
                                ForEach(groupedExpenses, id: \.0) { day, expensesInDay in
                                    VStack(alignment: .leading, spacing: 8) {
                                        // Day Header
                                        HStack {
                                            Text(day.relativeDayString)
                                                .font(SNTypography.sectionHeader())
                                                .foregroundColor(Color.snInk)
                                            
                                            Spacer()
                                            
                                            let daySum = expensesInDay.reduce(0.0) { $0 + $1.amount }
                                            Text(daySum.toIndonesianRupiah)
                                                .font(SNTypography.caption())
                                                .fontWeight(.medium)
                                                .foregroundColor(Color.snInkSecondary)
                                        }
                                        .padding(.horizontal, 18)
                                        
                                        // Items in that day
                                        VStack(spacing: 8) {
                                            ForEach(expensesInDay) { expense in
                                                ExpenseRow(
                                                    expense: expense,
                                                    onTap: {
                                                        #if canImport(UIKit)
                                                        let feedback = UIImpactFeedbackGenerator(style: .light)
                                                        feedback.impactOccurred()
                                                        #endif
                                                        editingExpense = expense
                                                    },
                                                    onDelete: {
                                                        store.deleteExpense(expense)
                                                    }
                                                )
                                                .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                                                    Button(role: .destructive) {
                                                        store.deleteExpense(expense)
                                                    } label: {
                                                        Label("Hapus", systemImage: "trash")
                                                    }
                                                }
                                                .swipeActions(edge: .leading) {
                                                    Button {
                                                        editingExpense = expense
                                                    } label: {
                                                        Label("Ubah", systemImage: "pencil")
                                                    }
                                                    .tint(Color.snAccent)
                                                }
                                            }
                                        }
                                        .padding(.horizontal, 16)
                                    }
                                }
                            }
                        }
                        
                        Spacer()
                            .frame(height: 40)
                    }
                }
            }
            .searchable(
                text: $viewModel.searchText,
                placement: .navigationBarDrawer(displayMode: .automatic),
                prompt: "Cari catatan atau kategori..."
            )
            .navigationTitle("Riwayat")
            .navigationBarTitleDisplayMode(.large)
            .sheet(item: $editingExpense) { expense in
                EditExpenseSheet(expense: expense) { updated in
                    store.updateExpense(updated)
                }
                .presentationDetents([.fraction(0.88), .large])
                .presentationDragIndicator(.visible)
            }
        }
    }
}
