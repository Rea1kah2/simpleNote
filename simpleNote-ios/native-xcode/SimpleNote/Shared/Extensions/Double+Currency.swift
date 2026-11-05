import Foundation

public extension Double {
    /// Format angka ke Rupiah dengan pemisah ribuan titik (contoh: Rp 50.000)
    var toIndonesianRupiah: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "Rp "
        formatter.currencyGroupingSeparator = "."
        formatter.currencyDecimalSeparator = ","
        formatter.maximumFractionDigits = 0
        formatter.minimumFractionDigits = 0
        
        let number = NSNumber(value: self)
        if let formatted = formatter.string(from: number) {
            return formatted
        }
        // Fallback manual jika locale formatting bermasalah
        let intVal = Int(self)
        let rawFormatter = NumberFormatter()
        rawFormatter.numberStyle = .decimal
        rawFormatter.groupingSeparator = "."
        let formattedDigits = rawFormatter.string(from: NSNumber(value: intVal)) ?? "\(intVal)"
        return "Rp \(formattedDigits)"
    }
    
    /// Format angka ringkas untuk statistik (misal: "Rp 450rb", "Rp 1,2jt")
    var toCompactRupiah: String {
        if self >= 1_000_000_000 {
            let val = self / 1_000_000_000
            return String(format: "Rp %.1fM", val).replacingOccurrences(of: ".0M", with: "M")
        } else if self >= 1_000_000 {
            let val = self / 1_000_000
            return String(format: "Rp %.1fjt", val).replacingOccurrences(of: ".0jt", with: "jt")
        } else if self >= 10_000 {
            let val = self / 1_000
            return String(format: "Rp %.0frb", val)
        } else {
            return self.toIndonesianRupiah
        }
    }
}

public extension Int {
    var toIndonesianRupiah: String {
        Double(self).toIndonesianRupiah
    }
}
