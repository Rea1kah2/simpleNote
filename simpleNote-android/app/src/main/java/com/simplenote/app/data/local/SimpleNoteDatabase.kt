package com.simplenote.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.simplenote.app.data.model.CategoryEntity
import com.simplenote.app.data.model.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ExpenseEntity::class, CategoryEntity::class], version = 3, exportSchema = false)
abstract class SimpleNoteDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: SimpleNoteDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN updatedAt TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN type TEXT NOT NULL DEFAULT 'expense'")
                db.execSQL("ALTER TABLE categories ADD COLUMN type TEXT NOT NULL DEFAULT 'expense'")
                DEFAULT_INCOME_CATEGORIES.forEach { cat ->
                    db.execSQL("INSERT OR IGNORE INTO categories (id, name, icon, isDefault, type) VALUES ('${cat.id}', '${cat.name}', '${cat.icon}', 1, 'income')")
                }
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): SimpleNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimpleNoteDatabase::class.java,
                    "simplenote_db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigrationOnDowngrade()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity("cat_food", "Makan & Minum", "utensils", isDefault = true, type = "expense"),
            CategoryEntity("cat_trans", "Transportasi", "car", isDefault = true, type = "expense"),
            CategoryEntity("cat_shop", "Belanja", "shopping-bag", isDefault = true, type = "expense"),
            CategoryEntity("cat_bill", "Tagihan", "receipt", isDefault = true, type = "expense"),
            CategoryEntity("cat_fun", "Hiburan", "ticket", isDefault = true, type = "expense"),
            CategoryEntity("cat_coffee", "Kopi & Jajan", "coffee", isDefault = true, type = "expense"),
            CategoryEntity("cat_health", "Kesehatan", "pill", isDefault = true, type = "expense"),
            CategoryEntity("cat_edu", "Pendidikan", "book", isDefault = true, type = "expense"),
            CategoryEntity("cat_charity", "Donasi", "heart", isDefault = true, type = "expense"),
            CategoryEntity("cat_other", "Lainnya", "sparkles", isDefault = true, type = "expense")
        )

        val DEFAULT_INCOME_CATEGORIES = listOf(
            CategoryEntity("inc_salary", "Gaji", "briefcase", isDefault = true, type = "income"),
            CategoryEntity("inc_freelance", "Freelance", "zap", isDefault = true, type = "income"),
            CategoryEntity("inc_bonus", "Bonus", "gift", isDefault = true, type = "income"),
            CategoryEntity("inc_gift", "Hadiah", "heart", isDefault = true, type = "income"),
            CategoryEntity("inc_business", "Usaha", "home", isDefault = true, type = "income"),
            CategoryEntity("inc_invest", "Investasi", "wallet", isDefault = true, type = "income"),
            CategoryEntity("inc_other", "Lainnya", "sparkles", isDefault = true, type = "income")
        )

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        database.categoryDao().insertAll(DEFAULT_CATEGORIES + DEFAULT_INCOME_CATEGORIES)
                    }
                }
            }
        }
    }
}
