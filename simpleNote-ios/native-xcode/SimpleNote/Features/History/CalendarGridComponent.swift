import SwiftUI

public struct CalendarGridComponent: View {
    public let month: Date
    @Binding public var selectedDate: Date?
    public let activeDays: Set<Date>
    public let onPreviousMonth: () -> Void
    public let onNextMonth: () -> Void
    
    private let calendar = Calendar.current
    private let weekdays = ["Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"]
    
    public init(
        month: Date,
        selectedDate: Binding<Date?>,
        activeDays: Set<Date>,
        onPreviousMonth: @escaping () -> Void,
        onNextMonth: @escaping () -> Void
    ) {
        self.month = month
        self._selectedDate = selectedDate
        self.activeDays = activeDays
        self.onPreviousMonth = onPreviousMonth
        self.onNextMonth = onNextMonth
    }
    
    private var daysInMonth: [Date?] {
        guard let monthInterval = calendar.dateInterval(of: .month, for: month),
              let firstDay = calendar.date(from: calendar.dateComponents([.year, .month], from: monthInterval.start)) else {
            return []
        }
        
        let firstWeekday = calendar.component(.weekday, from: firstDay) // 1 = Sunday
        let totalDays = calendar.range(of: .day, in: .month, for: month)?.count ?? 30
        
        var days: [Date?] = Array(repeating: nil, count: firstWeekday - 1)
        for day in 1...totalDays {
            if let date = calendar.date(byAdding: .day, value: day - 1, to: firstDay) {
                days.append(date)
            }
        }
        return days
    }
    
    public var body: some View {
        VStack(spacing: 12) {
            // Month Header with Prev/Next buttons
            HStack {
                Button(action: onPreviousMonth) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(Color.snInk)
                        .padding(8)
                        .background(Circle().fill(Color.snSurface))
                }
                
                Spacer()
                
                Text(month.monthAndYearString)
                    .font(SNTypography.sectionHeader())
                    .foregroundColor(Color.snInk)
                
                Spacer()
                
                Button(action: onNextMonth) {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(Color.snInk)
                        .padding(8)
                        .background(Circle().fill(Color.snSurface))
                }
            }
            .padding(.horizontal, 8)
            
            // Weekday symbols
            HStack {
                ForEach(weekdays, id: \.self) { day in
                    Text(day)
                        .font(SNTypography.micro())
                        .foregroundColor(Color.snInkSecondary)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.vertical, 2)
            
            // Days Grid
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 4), count: 7), spacing: 6) {
                ForEach(0..<daysInMonth.count, id: \.self) { index in
                    if let date = daysInMonth[index] {
                        DayCell(
                            date: date,
                            isSelected: isSelected(date),
                            hasExpenses: activeDays.contains(date.startOfDay),
                            isToday: date.isToday
                        ) {
                            handleDayTap(date)
                        }
                    } else {
                        Color.clear
                            .frame(height: 38)
                    }
                }
            }
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(Color.snSurface)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(Color.snBorder, lineWidth: 0.8)
        )
    }
    
    private func isSelected(_ date: Date) -> Bool {
        guard let selected = selectedDate else { return false }
        return calendar.isDate(date, inSameDayAs: selected)
    }
    
    private func handleDayTap(_ date: Date) {
        #if canImport(UIKit)
        let feedback = UIImpactFeedbackGenerator(style: .light)
        feedback.impactOccurred()
        #endif
        
        if isSelected(date) {
            selectedDate = nil // Toggle deselect
        } else {
            selectedDate = date
        }
    }
}

// MARK: - Day Cell
private struct DayCell: View {
    let date: Date
    let isSelected: Bool
    let hasExpenses: Bool
    let isToday: Bool
    let action: () -> Void
    
    var dayNumber: String {
        let calendar = Calendar.current
        return "\(calendar.component(.day, from: date))"
    }
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 3) {
                Text(dayNumber)
                    .font(.system(size: 14, weight: isSelected || isToday ? .bold : .regular, design: .rounded))
                    .foregroundColor(
                        isSelected ? .white :
                        (isToday ? Color.snAccent : Color.snInk)
                    )
                
                // Active spending indicator dot
                Circle()
                    .fill(isSelected ? Color.white : (hasExpenses ? Color.snAccent : Color.clear))
                    .frame(width: 4, height: 4)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 38)
            .background(
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(isSelected ? Color.snAccent : (isToday ? Color.snAccentSoft.opacity(0.7) : Color.clear))
            )
        }
        .buttonStyle(.plain)
    }
}
