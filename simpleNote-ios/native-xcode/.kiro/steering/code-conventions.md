# Code Conventions: SimpleNote

## Naming Conventions
- **Swift Files:** PascalCase, deskriptif sesuai fungsinya (contoh: `ExpenseRow.swift`, `CategoryChip.swift`).
- **Views:** Berakhiran `View` atau `Sheet` (contoh: `TodayView`, `AddExpenseSheet`).
- **ViewModels:** Menggunakan `@Observable` class dan berakhiran `ViewModel` (contoh: `TodayViewModel`).
- **Models:** Bentuk tunggal kata benda (contoh: `Expense`, `Category`).
- **Variables & Functions:** camelCase, self-documenting (contoh: `formattedAmount`, `fetchDailyExpenses()`).

## Swift & SwiftUI Idioms
1. **iOS 17+ Modern Macro:**
   - Gunakan `@Observable` pada ViewModel, bukan `ObservableObject` / `@Published`.
   - Di View, gunakan `@Bindable var viewModel = ...` atau `@State var viewModel = ...`.
2. **SwiftData Rules:**
   - Model kelas dianotasi `@Model` dengan default initializers lengkap.
   - Gunakan relationship dengan deleteRule yang tepat: `@Relationship(deleteRule: .nullify, inverse: \Expense.category)`.
   - Jangan simpan data persisten di `UserDefaults` atau global variables.
3. **Safety & Zero Force Unwrap:**
   - Dilarang keras menggunakan force unwrap (`!`), kecuali dalam test fixtures terkontrol.
   - Gunakan `guard let` atau optional chaining (`?.`) dengan fallback yang jelas.
4. **Localization & Copywriting:**
   - Semua label UI dan copy dibuat dalam Bahasa Indonesia yang hangat, bersahabat, dan ringkas.
   - Sesuai prinsip *calm design*: hindari kata-kata penghakiman ("boros", "terlalu banyak"), gunakan kata yang objektif ("Total pengeluaran hari ini", "Belum ada transaksi").

## Styling & Design Tokens Rules (Impeccable & Taste-Skill)
- Gunakan semantic color tokens dari `ColorTokens.swift` (`Color.snGround`, `Color.snAccent`, dll).
- Dilarang menggunakan shadow berlebihan atau warna gradien mencolok pada container utama.
- Gunakan ruang kosong (*whitespace* terukur) dan perbedaan *background tone* halus untuk memisahkan section.
- Tombol aksi utama (Save, Add) menggunakan warna aksen sage green (`#2D6A4F`).
- Nominal angka selalu menggunakan `.rounded` typography design agar nyaman dibaca.
