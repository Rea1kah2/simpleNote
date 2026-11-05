import SwiftUI
import Charts

public struct DailyTrendBarChart: View {
    public let dailyStats: [DailySpendStat]
    public let averageDaily: Double
    
    public init(dailyStats: [DailySpendStat], averageDaily: Double) {
        self.dailyStats = dailyStats
        self.averageDaily = averageDaily
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Tren Harian")
                        .font(SNTypography.sectionHeader())
                        .foregroundColor(Color.snInk)
                    Text("Rata-rata: \(averageDaily.toCompactRupiah)/hari")
                        .font(SNTypography.caption())
                        .foregroundColor(Color.snInkSecondary)
                }
                Spacer()
            }
            
            Chart {
                ForEach(dailyStats) { stat in
                    BarMark(
                        x: .value("Tanggal", stat.dayNumber),
                        y: .value("Nominal", stat.amount)
                    )
                    .foregroundStyle(stat.amount > 0 ? Color.snBar : Color.snBorder.opacity(0.3))
                    .cornerRadius(3)
                }
                
                if averageDaily > 0 {
                    RuleMark(y: .value("Rata-rata", averageDaily))
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 4]))
                        .foregroundStyle(Color.snInkSecondary.opacity(0.5))
                }
            }
            .chartXAxis {
                AxisMarks(values: .stride(by: 5)) { val in
                    AxisValueLabel()
                        .font(SNTypography.micro())
                        .foregroundStyle(Color.snInkSecondary)
                }
            }
            .chartYAxis {
                AxisMarks(position: .leading) { val in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                        .foregroundStyle(Color.snBorder)
                    AxisValueLabel {
                        if let intVal = val.as(Double.self) {
                            Text(intVal.toCompactRupiah)
                                .font(SNTypography.micro())
                                .foregroundStyle(Color.snInkSecondary)
                        }
                    }
                }
            }
            .frame(height: 180)
        }
        .padding(18)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(Color.snSurface)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(Color.snBorder, lineWidth: 0.8)
        )
    }
}
