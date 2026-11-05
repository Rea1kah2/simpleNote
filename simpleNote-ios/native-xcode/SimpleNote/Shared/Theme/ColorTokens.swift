import SwiftUI

// MARK: - Design System Color Tokens (Calm & Focused)
// Crafted in alignment with impeccable and design-taste-frontend guidelines.
// Eliminates stressful high-contrast alerts and AI-default cliches.

public extension Color {
    // MARK: - Sprint 2: Oranye Terracotta palette
    // Warm, bold, karakterful — bukan AI default palette
    
    /// Background utama — kertas krem warm
    static let snCanvas = Color(lightHex: "F8F5F0", darkHex: "141210")
    
    /// Kartu dan container permukaan
    static let snSurface = Color(lightHex: "FFFFFF", darkHex: "1E1B18")
    
    /// Teks utama / angka primer — warm near-black
    static let snInk = Color(lightHex: "1C1917", darkHex: "F5F0EB")
    
    /// Teks sekunder, keterangan tanggal & label pelengkap
    static let snInkSecondary = Color(lightHex: "78716C", darkHex: "A09490")
    
    /// Aksen utama — Oranye Terracotta: bold, hangat, berbeda dari AI default
    static let snAccent = Color(lightHex: "C1440E", darkHex: "F97316")
    
    /// Latar belakang aksen lembut (chip aktif & badge sorotan)
    static let snAccentSoft = Color(lightHex: "FEF3EE", darkHex: "2D1810")
    
    /// Pembatas garis halus — warm-toned border
    static let snBorder = Color(lightHex: "E8E0D8", darkHex: "2C2825")
    
    /// Warna grafik batang tren pengeluaran — amber hangat
    static let snBar = Color(lightHex: "D97706", darkHex: "FBBF24")
}

// MARK: - Hex Initializer Helper
public extension Color {
    init(lightHex: String, darkHex: String) {
        #if canImport(UIKit)
        self.init(UIColor { traitCollection in
            traitCollection.userInterfaceStyle == .dark
                ? UIColor(hex: darkHex)
                : UIColor(hex: lightHex)
        })
        #else
        self.init(hex: lightHex)
        #endif
    }
    
    init(hex: String) {
        let scanner = Scanner(string: hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted))
        var int: UInt64 = 0
        scanner.scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted).count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

#if canImport(UIKit)
import UIKit

public extension UIColor {
    convenience init(hex: String) {
        let scanner = Scanner(string: hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted))
        var int: UInt64 = 0
        scanner.scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted).count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            red: CGFloat(r) / 255,
            green: CGFloat(g) / 255,
            blue: CGFloat(b) / 255,
            alpha: CGFloat(a) / 255
        )
    }
}
#endif
