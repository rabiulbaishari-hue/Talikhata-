package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CashEntry
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.model.Supplier
import com.example.data.model.SupplierTransaction
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Update
    suspend fun update(profile: UserProfile)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY updatedAt DESC")
    fun getAllCustomersFlow(): Flow<List<Customer>>

    @Query("SELECT * FROM customers ORDER BY updatedAt DESC")
    suspend fun getAllCustomers(): List<Customer>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun getCustomerByIdFlow(id: Long): Flow<Customer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("UPDATE customers SET totalDue = :newDue, updatedAt = :timestamp WHERE id = :customerId")
    suspend fun updateDue(customerId: Long, newDue: Double, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface CustomerTransactionDao {
    @Query("SELECT * FROM customer_transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomerFlow(customerId: Long): Flow<List<CustomerTransaction>>

    @Query("SELECT * FROM customer_transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    suspend fun getTransactionsForCustomer(customerId: Long): List<CustomerTransaction>

    @Query("SELECT * FROM customer_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<CustomerTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CustomerTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: CustomerTransaction)

    @Query("DELETE FROM customer_transactions WHERE customerId = :customerId")
    suspend fun deleteForCustomer(customerId: Long)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY updatedAt DESC")
    fun getAllSuppliersFlow(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers ORDER BY updatedAt DESC")
    suspend fun getAllSuppliers(): List<Supplier>

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    suspend fun getSupplierById(id: Long): Supplier?

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    fun getSupplierByIdFlow(id: Long): Flow<Supplier?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    @Query("UPDATE suppliers SET totalDue = :newDue, updatedAt = :timestamp WHERE id = :supplierId")
    suspend fun updateDue(supplierId: Long, newDue: Double, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface SupplierTransactionDao {
    @Query("SELECT * FROM supplier_transactions WHERE supplierId = :supplierId ORDER BY timestamp DESC")
    fun getTransactionsForSupplierFlow(supplierId: Long): Flow<List<SupplierTransaction>>

    @Query("SELECT * FROM supplier_transactions WHERE supplierId = :supplierId ORDER BY timestamp DESC")
    suspend fun getTransactionsForSupplier(supplierId: Long): List<SupplierTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SupplierTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: SupplierTransaction)

    @Query("DELETE FROM supplier_transactions WHERE supplierId = :supplierId")
    suspend fun deleteForSupplier(supplierId: Long)
}

@Dao
interface CashEntryDao {
    @Query("SELECT * FROM cash_entries ORDER BY timestamp DESC")
    fun getAllCashEntriesFlow(): Flow<List<CashEntry>>

    @Query("SELECT * FROM cash_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getCashEntriesBetweenFlow(startTime: Long, endTime: Long): Flow<List<CashEntry>>

    @Query("SELECT * FROM cash_entries ORDER BY timestamp DESC")
    suspend fun getAllCashEntries(): List<CashEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashEntry(entry: CashEntry): Long

    @Delete
    suspend fun deleteCashEntry(entry: CashEntry)
}
