package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CashEntryDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.CustomerTransactionDao
import com.example.data.dao.SupplierDao
import com.example.data.dao.SupplierTransactionDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.CashEntry
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.model.Supplier
import com.example.data.model.SupplierTransaction
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        Customer::class,
        CustomerTransaction::class,
        Supplier::class,
        SupplierTransaction::class,
        CashEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun customerDao(): CustomerDao
    abstract fun customerTransactionDao(): CustomerTransactionDao
    abstract fun supplierDao(): SupplierDao
    abstract fun supplierTransactionDao(): SupplierTransactionDao
    abstract fun cashEntryDao(): CashEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tallykhata_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
