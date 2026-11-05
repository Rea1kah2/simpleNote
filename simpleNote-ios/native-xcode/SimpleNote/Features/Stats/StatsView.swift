import SwiftUI

public struct StatsView: View {
    @EnvironmentObject private var store: ExpenseStore
    @StateObject private var viewModel = StatsViewModel()
    
    private var monthlyTotal: Double {
        viewModel.calculateTotal(from: store.expenses)
    }
    
    private var categoryStats: [CategoryStat] {
        viewModel.calculateCategoryStats(from: store.expenses)
    }
    
    private var dailyTrend: [DailySpendStat] {
        viewModel.calculateDailyTrend(from: store.expenses)
    }
    
    private var averageDaily: Double {
        viewModel.calculateAverageDaily(from: store.expenses)
    }
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            ZStack {
                Color.snCanvas
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Month Selector Pill
                        HStack {
                            Button(action: { viewModel.previousMonth() }) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(Color.snInk)
                                    .padding(8)
                                    .background(Circle().fill(Color.snSurface))
                            }
                            
                            Spacer()
                            
                            Text(viewModel.selectedMonth.monthAndYearString)
                                .font(SNTypography.sectionHeader())
                                .foregroundColor(Color.snInk)
                            
                            Spacer()
                            
                            Button(action: { viewModel.nextMonth() }) {
                                Image(systemName: "chevron.right")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(Color.snInk)
                                    .padding(8)
                                    .background(Circle().fill(Color.snSurface))
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                        
                        // Total Overview Card
                        VStack(alignment: .leading, spacing: 6) {
                            Text("TOTAL PENGELUARAN BULAN INI")
                                .font(SNTypography.micro())
                                .tracking(1.0)
                                .foregroundColor(Color.snInkSecondary)
                            
                            Text(monthlyTotal.toIndonesianRupiah)
                                .font(SNTypography.amountHeader())
                                .foregroundColor(Color.snInk)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(18)
                        .background(
                            RoundedRectangle(cornerRadius: 18, style: .continuous)
                                .fill(Color.snSurface)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 18, style: .continuous)
                                .stroke(Color.snBorder, lineWidth: 0.8)
                        )
                        .padding(.horizontal, 16)
                        
                        if categoryStats.isEmpty {
                            EmptyStateView(
                                title: "Belum Ada Data Statistik",
                                message: "Catat transaksi di bulan ini untuk melihat pembagian kategori dan tren pengeluaranmu.",
                                systemImage: "chart.pie.fill"
                            )
                            .padding(.top, 20)
                        } else {
                            // Category Donut Chart
                            CategoryDonutChart(
                                stats: categoryStats,
                                totalAmount: monthlyTotal
                            )
                            .padding(.horizontal, 16)
                            
                            // Daily Trend Bar Chart
                            DailyTrendBarChart(
                                dailyStats: dailyTrend,
                                averageDaily: averageDaily
                            )
                            .padding(.horizontal, 16)
                        }
                        
                        Spacer()
                            .frame(height: 40)
                    }
                }
            }
            .navigationTitle("Statistik")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}
