import SwiftUI

public struct NumericKeypadView: View {
    @Binding public var amountText: String
    public let onSave: () -> Void
    
    public init(amountText: Binding<String>, onSave: @escaping () -> Void) {
        self._amountText = amountText
        self.onSave = onSave
    }
    
    private let rows: [[KeypadKey]] = [
        [.digit("1"), .digit("2"), .digit("3")],
        [.digit("4"), .digit("5"), .digit("6")],
        [.digit("7"), .digit("8"), .digit("9")],
        [.thousands, .digit("0"), .backspace]
    ]
    
    public var body: some View {
        VStack(spacing: 8) {
            ForEach(0..<rows.count, id: \.self) { rowIndex in
                HStack(spacing: 8) {
                    ForEach(rows[rowIndex], id: \.id) { key in
                        KeypadButton(key: key) {
                            handleKeyPress(key)
                        }
                    }
                }
            }
        }
    }
    
    private func handleKeyPress(_ key: KeypadKey) {
        #if canImport(UIKit)
        let feedback = UIImpactFeedbackGenerator(style: .light)
        feedback.impactOccurred()
        #endif
        
        switch key {
        case .digit(let char):
            // Batas maksimal 10 digit (Rp 9.999.999.999) agar aman
            if amountText == "0" {
                amountText = char
            } else if amountText.count < 10 {
                amountText.append(char)
            }
        case .thousands:
            if !amountText.isEmpty && amountText != "0" && amountText.count <= 7 {
                amountText.append("000")
            }
        case .backspace:
            if !amountText.isEmpty {
                amountText.removeLast()
                if amountText.isEmpty {
                    amountText = "0"
                }
            }
        case .clear:
            amountText = "0"
        }
    }
}

// MARK: - Keypad Models
private enum KeypadKey: Identifiable, Equatable {
    case digit(String)
    case thousands
    case backspace
    case clear
    
    var id: String {
        switch self {
        case .digit(let val): return "digit_\(val)"
        case .thousands: return "thousands"
        case .backspace: return "backspace"
        case .clear: return "clear"
        }
    }
}

private struct KeypadButton: View {
    let key: KeypadKey
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            ZStack {
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.snSurface)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .stroke(Color.snBorder.opacity(0.8), lineWidth: 0.8)
                    )
                
                contentView
            }
            .frame(height: 52)
        }
        .buttonStyle(KeypadButtonStyle())
    }
    
    @ViewBuilder
    private var contentView: some View {
        switch key {
        case .digit(let val):
            Text(val)
                .font(.system(size: 22, weight: .medium, design: .rounded))
                .foregroundColor(Color.snInk)
        case .thousands:
            Text("000")
                .font(.system(size: 16, weight: .semibold, design: .rounded))
                .foregroundColor(Color.snAccent)
        case .backspace:
            Image(systemName: "delete.left")
                .font(.system(size: 18, weight: .regular))
                .foregroundColor(Color.snInkSecondary)
        case .clear:
            Text("C")
                .font(.system(size: 18, weight: .semibold, design: .rounded))
                .foregroundColor(Color.snInkSecondary)
        }
    }
}

private struct KeypadButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.96 : 1.0)
            .opacity(configuration.isPressed ? 0.8 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}
