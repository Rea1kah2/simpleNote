import SwiftUI

public struct ExpenseRow: View {
    public let expense: Expense
    public var onTap: (() -> Void)? = nil
    public var onDelete: (() -> Void)? = nil
    
    public init(expense: Expense, onTap: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
        self.expense = expense
        self.onTap = onTap
        self.onDelete = onDelete
    }
    
    public var body: some View {
        Button(action: { onTap?() }) {
            HStack(spacing: 12) {
                // Emoji — inline, no badge circle, clean newspaper-row style
                Text(expense.categoryEmoji)
                    .font(.system(size: 20))
                    .frame(width: 28)
                
                // Detail (Category & Note) — takes horizontal space
                VStack(alignment: .leading, spacing: 2) {
                    Text(expense.categoryName)
                        .font(SNTypography.body())
                        .fontWeight(.medium)
                        .foregroundColor(Color.snInk)
                    
                    if !expense.note.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        Text(expense.note)
                            .font(SNTypography.caption())
                            .foregroundColor(Color.snInkSecondary)
                            .lineLimit(1)
                    }
                }
                
                Spacer()
                
                // Amount & Timestamp — right-aligned
                VStack(alignment: .trailing, spacing: 2) {
                    Text(expense.formattedAmount)
                        .font(SNTypography.amountRow())
                        .foregroundColor(Color.snInk)
                    
                    Text(expense.timestamp.formattedTime)
                        .font(SNTypography.micro())
                        .foregroundColor(Color.snInkSecondary)
                }
            }
            .padding(.vertical, 10)
            .padding(.horizontal, 14)
            .background(Color.snSurface)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
