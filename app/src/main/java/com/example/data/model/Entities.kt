package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val shopName: String = "রবিউল ট্রেডার্স",
    val ownerName: String = "এমডি রবিউল ইসলাম",
    val phone: String = "01700000000",
    val email: String = "rabiul.baishari@gmail.com",
    val passwordHash: String = "1234",
    val isPasswordSet: Boolean = false,
    val isBiometricEnabled: Boolean = true,
    val isCloudSyncEnabled: Boolean = true,
    val lastBackupTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String = "",
    val totalDue: Double = 0.0, // Positive = We get money (পাবো), Negative = Advance (জমা)
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_transactions")
data class CustomerTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val type: String, // "DUE" (বাকি দিলাম) or "PAYMENT" (নগদ পেলাম)
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val receiptNumber: String = ""
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val company: String = "",
    val totalDue: Double = 0.0, // Positive = We owe supplier (দেনা / তারা পাবে)
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "supplier_transactions")
data class SupplierTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val supplierId: Long,
    val amount: Double,
    val type: String, // "PURCHASE_DUE" (বাকি নিলাম) or "PAID" (পরিশোধ করলাম)
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cash_entries")
data class CashEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "CASH_IN" (নগদ গ্রহণ) or "CASH_OUT" (নগদ খরচ)
    val category: String, // e.g. "নগদ বিক্রি", "কাস্টমার জমা", "দোকান খরচ", "বিদ্যুৎ বিল", "ব্যক্তিগত"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
