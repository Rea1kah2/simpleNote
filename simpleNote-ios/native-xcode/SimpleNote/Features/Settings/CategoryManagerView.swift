import SwiftUI

public struct CategoryManagerView: View {
    @EnvironmentObject private var store: ExpenseStore
    
    @State private var isAddSheetPresented: Bool = false
    @State private var newCategoryName: String = ""
    @State private var newCategoryEmoji: String = "🏷️"
    
    private let commonEmojis = ["🏷️", "☕", "🍔", "🚕", "✈️", "🎬", "🛍️", "🏋️", "💻", "🐾", "📚", "🎨", "🌿", "⚡️"]
    
    public init() {}
    
    public var body: some View {
        List {
            Section {
                ForEach(store.categories) { category in
                    HStack(spacing: 12) {
                        Text(category.emoji)
                            .font(.system(size: 20))
                            .frame(width: 32, height: 32)
                            .background(Circle().fill(Color.snAccentSoft.opacity(0.5)))
                        
                        Text(category.name)
                            .font(SNTypography.body())
                            .foregroundColor(Color.snInk)
                        
                        Spacer()
                        
                        if category.isDefault {
                            Text("Bawaan")
                                .font(SNTypography.micro())
                                .foregroundColor(Color.snInkSecondary)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 3)
                                .background(Capsule().fill(Color.snBorder.opacity(0.5)))
                        }
                    }
                    .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                        if !category.isDefault {
                            Button(role: .destructive) {
                                store.deleteCategory(category)
                            } label: {
                                Label("Hapus", systemImage: "trash")
                            }
                        }
                    }
                }
            } header: {
                Text("Daftar Kategori")
                    .font(SNTypography.micro())
            } footer: {
                Text("Kategori bawaan dilindungi agar catatan keuangan tetap konsisten. Geser ke kiri untuk menghapus kategori kustom.")
                    .font(SNTypography.micro())
                    .foregroundColor(Color.snInkSecondary)
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle("Kelola Kategori")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: { isAddSheetPresented = true }) {
                    Image(systemName: "plus")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(Color.snAccent)
                }
            }
        }
        .sheet(isPresented: $isAddSheetPresented) {
            NavigationStack {
                Form {
                    Section("Emoji Kategori") {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 10) {
                                ForEach(commonEmojis, id: \.self) { emoji in
                                    Button(action: { newCategoryEmoji = emoji }) {
                                        Text(emoji)
                                            .font(.system(size: 24))
                                            .padding(8)
                                            .background(
                                                Circle()
                                                    .fill(newCategoryEmoji == emoji ? Color.snAccentSoft : Color.clear)
                                            )
                                    }
                                }
                            }
                            .padding(.vertical, 4)
                        }
                    }
                    
                    Section("Nama Kategori") {
                        TextField("Misal: Hewan Peliharaan", text: $newCategoryName)
                            .font(SNTypography.body())
                    }
                }
                .navigationTitle("Kategori Baru")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Batal") {
                            isAddSheetPresented = false
                            newCategoryName = ""
                        }
                    }
                    ToolbarItem(placement: .confirmationAction) {
                        Button("Tambah") {
                            saveNewCategory()
                        }
                        .disabled(newCategoryName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                        .fontWeight(.semibold)
                        .foregroundColor(Color.snAccent)
                    }
                }
            }
            .presentationDetents([.medium])
        }
    }
    
    private func saveNewCategory() {
        let trimmed = newCategoryName.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        
        store.addCategory(name: trimmed, emoji: newCategoryEmoji)
        newCategoryName = ""
        isAddSheetPresented = false
    }
}
