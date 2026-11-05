import SwiftUI

public struct CategoryChip: View {
    public let category: Category
    public let isSelected: Bool
    public let onSelect: () -> Void
    
    public init(category: Category, isSelected: Bool, onSelect: @escaping () -> Void) {
        self.category = category
        self.isSelected = isSelected
        self.onSelect = onSelect
    }
    
    public var body: some View {
        Button(action: {
            #if canImport(UIKit)
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.impactOccurred()
            #endif
            onSelect()
        }) {
            HStack(spacing: 6) {
                Text(category.emoji)
                    .font(.system(size: 16))
                Text(category.name)
                    .font(SNTypography.body())
                    .fontWeight(isSelected ? .semibold : .regular)
                    .foregroundColor(isSelected ? Color.snAccent : Color.snInk)
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(
                RoundedRectangle(cornerRadius: 20, style: .continuous)
                    .fill(isSelected ? Color.snAccentSoft : Color.snSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 20, style: .continuous)
                    .stroke(isSelected ? Color.snAccent : Color.snBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .animation(.easeInOut(duration: 0.18), value: isSelected)
    }
}
