# Design Specification: SimpleNote Core Tracker

## 1. Arsitektur Sistem

SimpleNote dibangun di atas pola arsitektur **MVVM (Model-View-ViewModel)** modern dengan deklarasi reaktif murni menggunakan framework SwiftUI dan SwiftData di iOS 17+.

```mermaid
graph TB
    subgraph UI ["Lapisan Tampilan (SwiftUI Views)"]
        TV[TodayView]
        HV[HistoryView]
        SV[StatsView]
        STV[SettingsView]
        AES[AddExpenseSheet]
    end

    subgraph ViewModels ["Lapisan Logik (Observable ViewModels)"]
        TVM[TodayViewModel]
        HVM[HistoryViewModel]
        SVM[StatsViewModel]
        STVM[SettingsViewModel]
    end

    subgraph DataLayer ["Lapisan Data (SwiftData)"]
        MC[ModelContext]
        EXP[Expense Model]
        CAT[Category Model]
        STORE[(Local SQLite Storage)]
    end

    TV --> TVM
    AES --> TVM
    HV --> HVM
    SV --> SVM
    STV --> STVM

    TVM --> MC
    HVM --> MC
    SVM --> MC
    STVM --> MC

    MC --> EXP
    MC --> CAT
    EXP --> STORE
    CAT --> STORE
```

## 2. Model Data & Skema SwiftData

### 2.1. Entitas Expense
```swift
@Model
final class Expense {
    @Attribute(.unique) var id: UUID
    var amount: Double
    var note: String
    var timestamp: Date
    var category: Category?

    init(amount: Double, note: String = "", timestamp: Date = .now, category: Category? = nil) {
        self.id = UUID()
        self.amount = amount
        self.note = note
        self.timestamp = timestamp
        self.category = category
    }
}
```

### 2.2. Entitas Category
```swift
@Model
final class Category {
    @Attribute(.unique) var id: UUID
    var name: String
    var emoji: String
    var isDefault: Bool
    var sortOrder: Int

    @Relationship(deleteRule: .nullify, inverse: \Expense.category)
    var expenses: [Expense]

    init(name: String, emoji: String, isDefault: Bool = false, sortOrder: Int = 0) {
        self.id = UUID()
        self.name = name
        self.emoji = emoji
        self.isDefault = isDefault
        self.sortOrder = sortOrder
        self.expenses = []
    }
}
```

### 2.3. Diagram Relasi Entitas (ERD)
```mermaid
erDiagram
    EXPENSE {
        UUID id PK
        Double amount
        String note
        Date timestamp
    }
    CATEGORY {
        UUID id PK
        String name
        String emoji
        Bool isDefault
        Int sortOrder
    }
    EXPENSE }o--o| CATEGORY : "belongs to"
```

## 3. Sequence Alur Pencatatan (Zero-Friction Fast Add)

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant TodayView
    participant AddExpenseSheet
    participant TodayViewModel
    participant ModelContext

    User->>TodayView: Ketuk Tombol Tambah (+)
    TodayView->>AddExpenseSheet: Buka sheet (.presentationDetents)
    AddExpenseSheet-->>User: Tampilkan Numpad & Pilihan Kategori
    User->>AddExpenseSheet: Ketik angka (misal: "45000")
    User->>AddExpenseSheet: Ketuk Kategori (misal: "🍜 Makan")
    User->>AddExpenseSheet: Ketuk Tombol "Simpan"
    AddExpenseSheet->>TodayViewModel: saveExpense(amount, category, note)
    TodayViewModel->>ModelContext: insert(newExpense)
    ModelContext-->>TodayViewModel: Sukses tersimpan
    TodayViewModel-->>TodayView: Data terefleksi otomatis
    AddExpenseSheet-->>User: Sheet tertutup halus (Haptic feedback)
```

## 4. Design System & Tokens (Calm & Focused)

### 4.1. Color Palette Tokens
- **`Color.snGround`** (`#F9F7F4` / Dark: `#121212`): Warna dasar latar belakang yang hangat, seperti kertas jurnal halus.
- **`Color.snSurface`** (`#FFFFFF` / Dark: `#1E1E1E`): Warna kartu dan elemen yang dapat disentuh.
- **`Color.snInk`** (`#1A1A1A` / Dark: `#F0F0F0`): Warna teks primer dengan kontras tinggi namun lembut.
- **`Color.snInkSecondary`** (`#6B6560` / Dark: `#A0A0A0`): Keterangan waktu, label, dan placeholder.
- **`Color.snAccent`** (`#2D6A4F` / Dark: `#52B788`): Sage green elegan untuk status aktif, tombol simpan, dan dot kalender.
- **`Color.snAccentSoft`** (`#D8F3DC` / Dark: `#1B4332`): Latar belakang chip kategori yang sedang dipilih.
- **`Color.snBorder`** (`#E8E4DF` / Dark: `#2C2C2C`): Garis pembatas tipis penegas struktur.

### 4.2. Typography Rules
- **Display Amount:** SF Pro Rounded, Bold, 34pt - 44pt (angka terasa ramah dan bersahabat).
- **Section Headers:** SF Pro Text, Semibold, 17pt.
- **List Items & Notes:** SF Pro Text, Regular, 15pt.
- **Subtext / Timestamps:** SF Pro Text, Regular, 13pt.
