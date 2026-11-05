import SwiftUI

public struct EditExpenseSheet: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var store: ExpenseStore
    
    public let expense: Expense
    public var onUpdate: ((Expense) -> Void)? = nil
    
    @State private var amountString: String = "0"
    @State private var selectedCategory: Category? = nil
    @State private var note: String = ""
    @State private var selectedDate: Date = .now
    @State private var showDatePicker: Bool = false
    @State private var showDeleteConfirmation: Bool = false
    
    public init(expense: Expense, onUpdate: ((Expense) -> Void)? = nil) {
        self.expense = expense
        self.onUpdate = onUpdate
        _amountString = State(initialValue: String(Int(expense.amount)))
        _note = State(initialValue: expense.note)
        _selectedDate = State(initialValue: expense.timestamp)
    }
    
    private var amountDouble: Double {
        Double(amountString) ?? 0.0
    }
    
    private var isSaveDisabled: Bool {
        amountDouble <= 0
    }
    
    public var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                // Top Display: Formatted Amount
                VStack(spacing: 4) {
                    Text(amountDouble.toIndonesianRupiah)
                        .font(SNTypography.amountHero())
                        .foregroundColor(amountDouble > 0 ? Color.snInk : Color.snInkSecondary.opacity(0.6))
                        .lineLimit(1)
                        .minimumScaleFactor(0.6)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .padding(.top, 12)
                    
                    // Date indicator & optional edit
                    Button(action: { showDatePicker.toggle() }) {
                        HStack(spacing: 4) {
                            Image(systemName: "calendar")
                                .font(.system(size: 12))
                            Text(selectedDate.isToday ? "Hari ini, \(selectedDate.formattedTime)" : selectedDate.relativeDayString)
                                .font(SNTypography.caption())
                        }
                        .foregroundColor(Color.snInkSecondary)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(
                            Capsule()
                                .fill(Color.snBorder.opacity(0.4))
                        )
                    }
                    .buttonStyle(.plain)
                    
                    if showDatePicker {
                        DatePicker(
                            "Waktu Transaksi",
                            selection: $selectedDate,
                            in: ...Date(),
                            displayedComponents: [.date, .hourAndMinute]
                        )
                        .datePickerStyle(.compact)
                        .labelsHidden()
                        .padding(.top, 4)
                    }
                }
                .padding(.horizontal, 16)
                
                // Category Chips (Horizontal Scrollable)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(store.categories) { category in
                            CategoryChip(
                                category: category,
                                isSelected: selectedCategory?.id == category.id
                            ) {
                                selectedCategory = category
                            }
                        }
                    }
                    .padding(.horizontal, 18)
                }
                
                // Note Input
                HStack(spacing: 10) {
                    Image(systemName: "pencil.line")
                        .foregroundColor(Color.snInkSecondary)
                    TextField("Catatan opsional", text: $note)
                        .font(SNTypography.body())
                        .foregroundColor(Color.snInk)
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.snSurface)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .stroke(Color.snBorder.opacity(0.7), lineWidth: 0.8)
                )
                .padding(.horizontal, 18)
                
                // Keypad
                NumericKeypadView(amountText: $amountString) {
                    handleUpdate()
                }
                .padding(.horizontal, 18)
                
                // Update Button
                Button(action: handleUpdate) {
                    HStack {
                        Image(systemName: "checkmark")
                            .font(.system(size: 16, weight: .bold))
                        Text("Simpan Perubahan")
                            .font(SNTypography.body())
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(
                        RoundedRectangle(cornerRadius: 14, style: .continuous)
                            .fill(isSaveDisabled ? Color.snBorder : Color.snAccent)
                    )
                    .foregroundColor(isSaveDisabled ? Color.snInkSecondary : .white)
                }
                .disabled(isSaveDisabled)
                .padding(.horizontal, 18)
                .padding(.bottom, 12)
            }
            .background(Color.snCanvas.ignoresSafeArea())
            .navigationTitle("Ubah Catatan")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Batal") {
                        dismiss()
                    }
                    .foregroundColor(Color.snInkSecondary)
                }
                
                ToolbarItem(placement: .destructiveAction) {
                    Button(role: .destructive) {
                        showDeleteConfirmation = true
                    } label: {
                        Image(systemName: "trash")
                            .foregroundColor(.red)
                    }
                }
            }
            .confirmationDialog("Hapus Catatan?", isPresented: $showDeleteConfirmation, titleVisibility: .visible) {
                Button("Hapus Catatan Ini", role: .destructive) {
                    store.deleteExpense(expense)
                    dismiss()
                }
                Button("Batal", role: .cancel) {}
            } message: {
                Text("Transaksi ini akan dihapus permanen dari riwayat pengeluaran.")
            }
            .onAppear {
                setupInitialCategory()
            }
        }
    }
    
    private func setupInitialCategory() {
        if let match = store.categories.first(where: { $0.id == expense.categoryId }) {
            selectedCategory = match
        } else if let matchByName = store.categories.first(where: { $0.name == expense.categoryName }) {
            selectedCategory = matchByName
        } else {
            selectedCategory = store.categories.first
        }
    }
    
    private func handleUpdate() {
        guard amountDouble > 0 else { return }
        
        let updated = Expense(
            id: expense.id,
            amount: amountDouble,
            note: note.trimmingCharacters(in: .whitespacesAndNewlines),
            timestamp: selectedDate,
            categoryId: selectedCategory?.id,
            categoryName: selectedCategory?.name ?? expense.categoryName,
            categoryEmoji: selectedCategory?.emoji ?? expense.categoryEmoji
        )
        
        if let onUpdate = onUpdate {
            onUpdate(updated)
        } else {
            store.updateExpense(updated)
        }
        
        dismiss()
    }
}
