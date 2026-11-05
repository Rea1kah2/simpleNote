import SwiftUI

public struct EmptyStateView: View {
    public let title: String
    public let message: String
    public let systemImage: String
    public var actionTitle: String? = nil
    public var onAction: (() -> Void)? = nil
    
    public init(
        title: String,
        message: String,
        systemImage: String = "leaf.fill",
        actionTitle: String? = nil,
        onAction: (() -> Void)? = nil
    ) {
        self.title = title
        self.message = message
        self.systemImage = systemImage
        self.actionTitle = actionTitle
        self.onAction = onAction
    }
    
    public var body: some View {
        VStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.snAccentSoft.opacity(0.5))
                    .frame(width: 68, height: 68)
                
                Image(systemName: systemImage)
                    .font(.system(size: 28, weight: .regular))
                    .foregroundColor(Color.snAccent)
            }
            .padding(.bottom, 6)
            
            Text(title)
                .font(SNTypography.sectionHeader())
                .foregroundColor(Color.snInk)
                .multilineTextAlignment(.center)
            
            Text(message)
                .font(SNTypography.caption())
                .foregroundColor(Color.snInkSecondary)
                .multilineTextAlignment(.center)
                .lineSpacing(3)
                .padding(.horizontal, 32)
            
            if let actionTitle = actionTitle, let onAction = onAction {
                Button(action: onAction) {
                    Text(actionTitle)
                        .font(SNTypography.body())
                        .fontWeight(.medium)
                        .foregroundColor(Color.snAccent)
                        .padding(.horizontal, 18)
                        .padding(.vertical, 8)
                        .background(
                            Capsule()
                                .fill(Color.snAccentSoft)
                        )
                }
                .padding(.top, 8)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 40)
    }
}
