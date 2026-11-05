import SwiftUI

public struct TodayView: View {
    @EnvironmentObject private var store: ExpenseStore
    @StateObject private var viewModel = TodayViewModel()
    @State private var editingExpense: Expense? = nil
    
    private var todayExpenses: [Expense] {
        viewModel.filterTodayExpenses(from: store.expenses)
    }
    
    private var todayTotal: Double {
        viewModel.calculateDailyTotal(from: store.expenses)
    }
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                Color.snCanvas
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        // Direct Canvas Hero Header (No boxed card — 52pt Black Rounded)
                        dailyCanvasHero
                            .padding(.horizontal, 20)
                            .padding(.top, 16)
                        
                        // Transaction Section
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Text("Aktivitas Hari Ini")
                                    .font(SNTypography.sectionHeader())
                                    .foregroundColor(Color.snInk)
                                
                                Spacer()
                                
                                if !todayExpenses.isEmpty {
                                    Text("\(todayExpenses.count) transaksi • ketuk untuk ubah")
                                        .font(SNTypography.caption())
                                        .foregroundColor(Color.snInkSecondary)
                                }
                            }
                            .padding(.horizontal, 20)
                            
                            if todayExpenses.isEmpty {
                                EmptyStateView(
                                    title: "Belum Ada Catatan Hari Ini",
                                    message: "Setiap pengeluaran kecil itu berharga. Ketuk tombol catat di bawah untuk memulai jurnal belanjamu hari ini.",
                                    systemImage: "cup.and.saucer.fill",
                                    actionTitle: "+ Catat Pengeluaran"
                                ) {
                                    viewModel.isAddExpenseSheetPresented = true
                                }
                                .padding(.horizontal, 16)
                            } else {
                                LazyVStack(spacing: 8) {
                                    ForEach(todayExpenses) { expense in
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
                        
                        // Bottom spacer so FAB doesn't cover list
                        Spacer()
                            .frame(height: 90)
                    }
                }
                
                // Quick Add Floating Action Button (FAB)
                Button(action: {
                    #if canImport(UIKit)
                    let feedback = UIImpactFeedbackGenerator(style: .medium)
                    feedback.impactOccurred()
                    #endif
                    viewModel.isAddExpenseSheetPresented = true
                }) {
                    HStack(spacing: 8) {
                        Image(systemName: "plus")
                            .font(.system(size: 17, weight: .bold))
                        Text("Catat")
                            .font(SNTypography.body())
                            .fontWeight(.semibold)
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 22)
                    .padding(.vertical, 14)
                    .background(
                        Capsule()
                            .fill(Color.snAccent)
                            .shadow(color: Color.snAccent.opacity(0.32), radius: 10, x: 0, y: 5)
                    )
                }
                .buttonStyle(.plain)
                .padding(.trailing, 20)
                .padding(.bottom, 20)
            }
            .navigationTitle("Hari Ini")
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $viewModel.isAddExpenseSheetPresented) {
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
            .sheet(item: $editingExpense) { expense in
                EditExpenseSheet(expense: expense) { updated in
                    store.updateExpense(updated)
                }
                .presentationDetents([.fraction(0.88), .large])
                .presentationDragIndicator(.visible)
            }
        }
    }
    
    // MARK: - Direct Canvas Hero Header (52pt Black Rounded)
    private var dailyCanvasHero: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(Date().formattedDayName.uppercased())
                    .font(SNTypography.micro())
                    .fontWeight(.bold)
                    .tracking(1.4)
                    .foregroundColor(Color.snAccent)
                
                Text("•")
                    .foregroundColor(Color.snInkSecondary.opacity(0.5))
                
                Text(Date().monthAndYearString)
                    .font(SNTypography.micro())
                    .foregroundColor(Color.snInkSecondary)
                
                Spacer()
            }
            
            // 52pt Dominant Hero Total
            Text(todayTotal.toIndonesianRupiah)
                .font(SNTypography.amountHeader())
                .foregroundColor(Color.snInk)
                .lineLimit(1)
                .minimumScaleFactor(0.6)
                .padding(.top, 2)
            
            // Signature Terracotta Accent Underline
            Rectangle()
                .fill(Color.snAccent)
                .frame(width: 48, height: 4)
                .cornerRadius(2)
                .padding(.top, 4)
                .padding(.bottom, 6)
            
            HStack(spacing: 6) {
                Circle()
                    .fill(todayTotal == 0 ? Color.snInkSecondary.opacity(0.5) : Color.snAccent)
                    .frame(width: 6, height: 6)
                
                Text(todayTotal == 0 ? "Dompetmu masih tenang hari ini" : "\(todayExpenses.count) pengeluaran tercatat secara sadar")
                    .font(SNTypography.caption())
                    .foregroundColor(Color.snInkSecondary)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
