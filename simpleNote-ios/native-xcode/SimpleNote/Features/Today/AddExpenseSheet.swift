import SwiftUI

public struct AddExpenseSheet: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var store: ExpenseStore
    
    private let onSave: (Double, Category?, String, Date) -> Void
    
    @State private var amountString: String = "0"
    @State private var selectedCategory: Category? = nil
    @State private var note: String = ""
    @State private var selectedDate: Date = .now
    @State private var showDatePicker: Bool = false
    
    public init(onSave: @escaping (Double, Category?, String, Date) -> Void) {
        self.onSave = onSave
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
                
                // Note Input (Single-line quiet input)
                HStack(spacing: 10) {
                    Image(systemName: "pencil.line")
                        .foregroundColor(Color.snInkSecondary)
                    TextField("Catatan opsional (misal: Makan siang geprek)", text: $note)
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
                    handleSave()
                }
                .padding(.horizontal, 18)
                
                // Save Button
                Button(action: handleSave) {
                    HStack {
                        Image(systemName: "checkmark")
                            .font(.system(size: 16, weight: .bold))
                        Text("Simpan Pengeluaran")
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
            .navigationTitle("Catat Baru")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Batal") {
                        dismiss()
                    }
                    .foregroundColor(Color.snInkSecondary)
                }
            }
            .onAppear {
                if selectedCategory == nil, let first = store.categories.first {
                    selectedCategory = first
                }
            }
        }
    }
    
    private func handleSave() {
        guard amountDouble > 0 else { return }
        onSave(amountDouble, selectedCategory, note, selectedDate)
        dismiss()
    }
}
