# Tech Stack: SimpleNote

## Core Architecture
- **Language:** Swift 5.7+ / Swift 5.9
- **UI Framework:** SwiftUI (iOS 16.0+)
- **Data Persistence:** Local JSON File Persistence (`ExpenseStore` with atomic file writes)
- **Visualisasi & Charts:** Swift Charts (Apple `Charts` BarMark) & Custom SwiftUI Donut Chart
- **Arsitektur Aplikasi:** Model-View-ViewModel (MVVM) dengan `ObservableObject` & `@Published`

## Whitelist (Komponen yang diizinkan)
- Framework first-party Apple: `SwiftUI`, `Charts`, `Foundation`, `Combine`, `OSLog`.
- SF Symbols bawaan sistem untuk iconography.
- SF Pro Rounded & SF Pro Display/Text untuk tipografi.

## Technical Constraints & Guidelines
- **Target OS:** iOS 16.0 minimum (kompatibel penuh dengan Xcode 14.2 & iOS 16/17/18).
- **Single Source of Truth:** `ExpenseStore` di-inject melalui `.environmentObject(store)` pada root `App`.
- **Locale & Format:** Format mata uang spesifik Rupiah (`Rp 50.000`) dengan pemisah ribuan titik (`.`) tanpa desimal sen secara default.
- **Offline First:** 100% data disimpan secara persisten lokal di disk peranti.
