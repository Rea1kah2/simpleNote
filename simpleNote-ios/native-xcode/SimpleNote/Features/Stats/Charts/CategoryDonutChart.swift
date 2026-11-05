import SwiftUI

public struct CategoryDonutChart: View {
    public let stats: [CategoryStat]
    public let totalAmount: Double
    
    public init(stats: [CategoryStat], totalAmount: Double) {
        self.stats = stats
        self.totalAmount = totalAmount
    }
    
    // Hitung segmen sudut untuk setiap kategori (0.0 sampai 1.0)
    private var segments: [(stat: CategoryStat, start: Double, end: Double)] {
        var result: [(CategoryStat, Double, Double)] = []
        var current: Double = 0.0
        
        for stat in stats {
            let portion = stat.percentage / 100.0
            let start = current
            let end = current + portion
            result.append((stat, start, end))
            current = end
        }
        return result
    }
    
    public var body: some View {
        VStack(spacing: 16) {
            // Donut Ring Visual
            ZStack {
                // Background Track
                Circle()
                    .stroke(Color.snBorder.opacity(0.4), lineWidth: 28)
                    .frame(width: 170, height: 170)
                
                // Segments
                ForEach(0..<segments.count, id: \.self) { idx in
                    let item = segments[idx]
                    Circle()
                        .trim(from: CGFloat(item.start), to: CGFloat(item.end))
                        .stroke(
                            item.stat.color,
                            style: StrokeStyle(lineWidth: 28, lineCap: .butt)
                        )
                        .rotationEffect(.degrees(-90))
                        .frame(width: 170, height: 170)
                }
                
                // Center Label
                VStack(spacing: 2) {
                    Text("TOTAL")
                        .font(SNTypography.micro())
                        .foregroundColor(Color.snInkSecondary)
                    Text(totalAmount.toCompactRupiah)
                        .font(SNTypography.amountRow())
                        .foregroundColor(Color.snInk)
                }
            }
            .frame(height: 190)
            .padding(.top, 8)
            
            // Legend list
            VStack(spacing: 8) {
                ForEach(stats) { stat in
                    HStack(spacing: 10) {
                        Circle()
                            .fill(stat.color)
                            .frame(width: 8, height: 8)
                        
                        Text("\(stat.emoji) \(stat.name)")
                            .font(SNTypography.body())
                            .foregroundColor(Color.snInk)
                        
                        Spacer()
                        
                        Text(String(format: "%.1f%%", stat.percentage))
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                        
                        Text(stat.amount.toIndonesianRupiah)
                            .font(SNTypography.body())
                            .fontWeight(.medium)
                            .foregroundColor(Color.snInk)
                    }
                    .padding(.vertical, 3)
                }
            }
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
