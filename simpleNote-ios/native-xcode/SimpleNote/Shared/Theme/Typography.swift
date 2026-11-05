import SwiftUI

// MARK: - Design System Typography
// SF Pro Rounded creates warmth for financial figures without feeling toy-like.
// SF Pro Text provides crisp legibility for notes, timestamps, and categories.

public struct SNTypography {
    /// Nominal utama besar di hero input (e.g. Rp 45.000)
    public static func amountHero() -> Font {
        .system(size: 40, weight: .bold, design: .rounded)
    }
    
    /// Nominal total di kartu header harian — 52pt Black Rounded, elemen dominan
    public static func amountHeader() -> Font {
        .system(size: 52, weight: .black, design: .rounded)
    }
    
    /// Nominal per baris transaksi
    public static func amountRow() -> Font {
        .system(size: 16, weight: .semibold, design: .rounded)
    }
    
    /// Judul halaman / tab
    public static func screenTitle() -> Font {
        .system(size: 24, weight: .semibold, design: .default)
    }
    
    /// Judul section atau header group tanggal
    public static func sectionHeader() -> Font {
        .system(size: 15, weight: .semibold, design: .default)
    }
    
    /// Teks nama kategori & catatan
    public static func body() -> Font {
        .system(size: 15, weight: .regular, design: .default)
    }
    
    /// Keterangan sekunder (jam, subtext)
    public static func caption() -> Font {
        .system(size: 13, weight: .regular, design: .default)
    }
    
    /// Label kecil atau badge
    public static func micro() -> Font {
        .system(size: 11, weight: .medium, design: .default)
    }
}
