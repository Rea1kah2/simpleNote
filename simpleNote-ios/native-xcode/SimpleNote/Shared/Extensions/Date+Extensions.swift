import Foundation

public extension Date {
    /// Awal hari (00:00:00) pada kalender lokal
    var startOfDay: Date {
        Calendar.current.startOfDay(for: self)
    }
    
    /// Akhir hari (23:59:59) pada kalender lokal
    var endOfDay: Date {
        var components = DateComponents()
        components.day = 1
        components.second = -1
        return Calendar.current.date(byAdding: components, to: self.startOfDay) ?? self
    }
    
    /// Awal bulan saat ini
    var startOfMonth: Date {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month], from: self)
        return calendar.date(from: components) ?? self
    }
    
    /// Akhir bulan saat ini
    var endOfMonth: Date {
        let calendar = Calendar.current
        if let nextMonth = calendar.date(byAdding: .month, value: 1, to: self.startOfMonth) {
            return calendar.date(byAdding: .second, value: -1, to: nextMonth) ?? self
        }
        return self
    }
    
    /// Cek apakah dua tanggal berada di hari yang sama
    func isSameDay(as other: Date) -> Bool {
        Calendar.current.isDate(self, inSameDayAs: other)
    }
    
    /// Cek apakah tanggal adalah hari ini
    var isToday: Bool {
        Calendar.current.isDateInToday(self)
    }
    
    /// Cek apakah tanggal adalah kemarin
    var isYesterday: Bool {
        Calendar.current.isDateInYesterday(self)
    }
    
    /// Format waktu jam:menit (e.g. 14:35)
    var formattedTime: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: self)
    }
    
    /// Format hari & tanggal lengkap (e.g. "Selasa, 4 Nov 2026" atau "Hari Ini")
    var relativeDayString: String {
        if self.isToday {
            return "Hari Ini"
        } else if self.isYesterday {
            return "Kemarin"
        } else {
            let formatter = DateFormatter()
            formatter.locale = Locale(identifier: "id_ID")
            formatter.dateFormat = "EEEE, d MMM yyyy"
            return formatter.string(from: self)
        }
    }
    
    /// Format nama bulan dan tahun (e.g. "November 2026")
    var monthAndYearString: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "id_ID")
        formatter.dateFormat = "MMMM yyyy"
        return formatter.string(from: self)
    }
    
    /// Format nama hari lengkap (e.g. "Rabu")
    var formattedDayName: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "id_ID")
        formatter.dateFormat = "EEEE"
        return formatter.string(from: self)
    }
}
