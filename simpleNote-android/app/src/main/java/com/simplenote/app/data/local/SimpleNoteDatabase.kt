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

@Database(entities = [ExpenseEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
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

        fun getDatabase(context: Context, scope: CoroutineScope): SimpleNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimpleNoteDatabase::class.java,
                    "simplenote_db"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity("cat_food", "Makan & Minum", "utensils", isDefault = true),
            CategoryEntity("cat_trans", "Transportasi", "car", isDefault = true),
            CategoryEntity("cat_shop", "Belanja", "shopping-bag", isDefault = true),
            CategoryEntity("cat_bill", "Tagihan", "receipt", isDefault = true),
            CategoryEntity("cat_fun", "Hiburan", "ticket", isDefault = true),
            CategoryEntity("cat_coffee", "Kopi & Jajan", "coffee", isDefault = true),
            CategoryEntity("cat_health", "Kesehatan", "pill", isDefault = true),
            CategoryEntity("cat_edu", "Pendidikan", "book", isDefault = true),
            CategoryEntity("cat_charity", "Donasi", "heart", isDefault = true),
            CategoryEntity("cat_other", "Lainnya", "sparkles", isDefault = true)
        )

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        database.categoryDao().insertAll(DEFAULT_CATEGORIES)
                    }
                }
            }
        }
    }
}
