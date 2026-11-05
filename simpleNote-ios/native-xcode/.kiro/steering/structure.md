# Directory Structure & Architecture: SimpleNote

## Directory Layout
```
catat-simpel/
├── .kiro/
│   └── steering/
│       ├── product.md
│       ├── tech.md
│       ├── structure.md
│       └── code-conventions.md
└── SimpleNote/
    ├── App/
    │   ├── SimpleNoteApp.swift           # Application entry point & ModelContainer setup
    │   └── ModelContainerFactory.swift   # SwiftData schema definition & default seeder
    ├── Models/
    │   ├── Expense.swift                  # SwiftData @Model for individual transaction
    │   └── Category.swift                 # SwiftData @Model for expense category
    ├── Features/
    │   ├── Today/
    │   │   ├── TodayView.swift            # Primary daily dashboard & quick summary
    │   │   ├── TodayViewModel.swift       # Daily business logic & filtering
    │   │   └── AddExpenseSheet.swift      # Ultra-fast zero-friction input bottom sheet
    │   ├── History/
    │   │   ├── HistoryView.swift          # Monthly calendar & transaction timeline
    │   │   ├── HistoryViewModel.swift     # Date-based grouping & monthly pagination
    │   │   └── CalendarGridComponent.swift # Custom calendar grid with activity dot indicators
    │   ├── Stats/
    │   │   ├── StatsView.swift            # Categorical break-down & trend analytics
    │   │   ├── StatsViewModel.swift       # Aggregation, percentages & daily spending calculations
    │   │   └── Charts/
    │   │       ├── CategoryDonutChart.swift # Apple Charts pie/donut representation
    │   │       └── DailyTrendBarChart.swift  # Apple Charts daily spending bar representation
    │   └── Settings/
    │       ├── SettingsView.swift         # App information, currency preview, data management
    │       └── CategoryManagerView.swift  # Add, edit, reorder or delete categories
    ├── Shared/
    │   ├── Components/
    │   │   ├── ExpenseRow.swift           # Standard list item presentation
    │   │   ├── CategoryChip.swift         # Horizontal scrollable category pill
    │   │   ├── NumericKeypadView.swift    # Custom high-speed touch keypad
    │   │   └── EmptyStateView.swift       # Friendly non-judgmental empty states
    │   ├── Extensions/
    │   │   ├── Date+Extensions.swift      # Calendar calculations (startOfDay, startOfMonth)
    │   │   └── Double+Currency.swift      # Indonesian Rupiah formatting helper
    │   └── Theme/
    │       ├── ColorTokens.swift          # Sage green palette, ground, ink, and surfaces
    │       └── Typography.swift           # SF Pro Rounded / Display font system
    └── SimpleNoteTests/
        ├── ExpenseModelTests.swift
        ├── StatsCalculationTests.swift
        └── DateHelpersTests.swift
```

## Architectural Conventions
1. **MVVM Pattern:**
   - Views adalah representasi deklaratif UI murni.
   - ViewModels menggunakan `@Observable` (iOS 17+) untuk mengelola state dan logik bisnis.
   - Operasi SwiftData (`insert`, `delete`, `save`) dikoordinasikan secara rapi.
2. **Component Boundaries:**
   - Komponen visual independen disimpan di `Shared/Components/`.
   - Fitur spesifik terisolasi di dalam `Features/[FeatureName]/`.
   - Modifikasi kategori dilakukan secara terpisah di Settings agar tidak menambah friksi pada alur pencatatan harian.
