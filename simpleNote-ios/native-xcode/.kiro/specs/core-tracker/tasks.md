# Tasks: SimpleNote Core Implementation

- [x] 1. Project Initialization & Foundation Setup
  - [x] 1.1 Generate steering files (`product.md`, `tech.md`, `structure.md`, `code-conventions.md`)
  - [x] 1.2 Formulate requirements and design specification (`requirements.md`, `design.md`)
  - [x] 1.3 Initialize Xcode Project structure for `SimpleNote` with bundle identifier `com.yourname.simplenote`
  - [x] 1.4 Setup Theme Tokens (`ColorTokens.swift` & `Typography.swift`)
  - [x] 1.5 Implement formatting & calculation extensions (`Double+Currency.swift`, `Date+Extensions.swift`)

- [x] 2. Data Persistence Layer (SwiftData)
  - [x] 2.1 Implement `Category.swift` model with `@Model` and default category definitions
  - [x] 2.2 Implement `Expense.swift` model with `@Model` and relationship to `Category`
  - [x] 2.3 Create `ModelContainerFactory.swift` for container initialization & initial default seeding

- [x] 3. Shared UI Components
  - [x] 3.1 Build `CategoryChip.swift` with selection feedback and haptic support
  - [x] 3.2 Build `NumericKeypadView.swift` for ultra-fast friction-free entry
  - [x] 3.3 Build `ExpenseRow.swift` with clean spacing, category emoji, note, and formatted amount
  - [x] 3.4 Build `EmptyStateView.swift` for calm empty states

- [x] 4. Core Feature: Today (Daily Activity & Ultra-Fast Entry)
  - [x] 4.1 Implement `TodayViewModel.swift` with daily sum calculation and expense operations
  - [x] 4.2 Build `AddExpenseSheet.swift` with dynamic keypad, direct category tap, and instant save
  - [x] 4.3 Build `TodayView.swift` containing daily total header, transaction list, and quick add FAB

- [x] 5. Core Feature: History (Timeline & Activity Calendar)
  - [x] 5.1 Implement `HistoryViewModel.swift` with month navigation and day grouping
  - [x] 5.2 Build `CalendarGridComponent.swift` showing days of month and active spending indicator dots
  - [x] 5.3 Build `HistoryView.swift` linking calendar interaction with grouped daily transaction feed

- [x] 6. Core Feature: Stats (Analytics & Visual Distribution)
  - [x] 6.1 Implement `StatsViewModel.swift` computing total monthly expenditure, percentage per category, and daily series
  - [x] 6.2 Build `CategoryDonutChart.swift` using Apple Swift Charts
  - [x] 6.3 Build `DailyTrendBarChart.swift` showing daily expense bars
  - [x] 6.4 Build `StatsView.swift` combining monthly metrics, donut chart, and trend chart

- [x] 7. Core Feature: Settings & Category Customization
  - [x] 7.1 Implement `SettingsView.swift` with preferences preview, reset option, and app info
  - [x] 7.2 Implement `CategoryManagerView.swift` with ability to add new custom categories and delete non-default categories

- [x] 8. App Shell & Root Navigation
  - [x] 8.1 Implement `MainTabView.swift` with 4 tabs: Hari Ini, Riwayat, Statistik, Pengaturan
  - [x] 8.2 Integrate root `SimpleNoteApp.swift` with `.modelContainer` and navigation lifecycle

- [x] 9. Automated Testing & Verification
  - [x] 9.1 Unit tests for `Double+Currency` and `Date+Extensions`
  - [x] 9.2 Unit tests for `StatsViewModel` data aggregations
  - [x] 9.3 End-to-end code inspection & quality check against Impeccable and Taste-Skill rules

- [x] 10. Sprint 2: Visual Craftsmanship Overhaul & Power Feature Pack
  - [x] 10.1 Update Theme Tokens to Oranye Terracotta palette (`ColorTokens.swift`) and bold 52pt hierarchy (`Typography.swift`)
  - [x] 10.2 Redesign `ExpenseRow.swift` (newspaper row style, horizontal readability, tap-to-edit support)
  - [x] 10.3 Redesign `TodayView.swift` (direct canvas total, dynamic headline, seamless FAB)
  - [x] 10.4 Implement `EditExpenseSheet.swift` and `ExpenseStore.updateExpense` for frictionless in-place editing
  - [x] 10.5 Implement search query and category filtering in `HistoryView.swift` and `HistoryViewModel.swift`
  - [x] 10.6 Implement smart daily local notification reminder (`NotificationManager.swift`, auto-cancel when already spent) and Settings toggle
  - [x] 10.7 Implement Home Screen Quick Action shortcut (`SimpleNoteApp.swift` / `Info.plist`)
  - [x] 10.8 Update Xcode project references (`project.pbxproj`) and end-to-end build verification
