package com.example.data.repository

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.model.CashEntry
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.model.Supplier
import com.example.data.model.SupplierTransaction
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TallyRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userProfileDao = db.userProfileDao()
    private val customerDao = db.customerDao()
    private val customerTxDao = db.customerTransactionDao()
    private val supplierDao = db.supplierDao()
    private val supplierTxDao = db.supplierTransactionDao()
    private val cashEntryDao = db.cashEntryDao()

    val profileFlow: Flow<UserProfile?> = userProfileDao.getProfileFlow()
    val customersFlow: Flow<List<Customer>> = customerDao.getAllCustomersFlow()
    val suppliersFlow: Flow<List<Supplier>> = supplierDao.getAllSuppliersFlow()
    val cashEntriesFlow: Flow<List<CashEntry>> = cashEntryDao.getAllCashEntriesFlow()

    suspend fun getProfile(): UserProfile? = withContext(Dispatchers.IO) {
        userProfileDao.getProfile()
    }

    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdate(profile)
    }

    suspend fun updateBiometricSetting(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = userProfileDao.getProfile() ?: UserProfile()
        userProfileDao.insertOrUpdate(current.copy(isBiometricEnabled = enabled))
    }

    suspend fun updatePassword(newPass: String) = withContext(Dispatchers.IO) {
        val current = userProfileDao.getProfile() ?: UserProfile()
        userProfileDao.insertOrUpdate(current.copy(passwordHash = newPass, isPasswordSet = true))
    }

    // Customer operations
    fun getTransactionsForCustomerFlow(customerId: Long): Flow<List<CustomerTransaction>> =
        customerTxDao.getTransactionsForCustomerFlow(customerId)

    suspend fun addCustomer(name: String, phone: String, address: String, initialDue: Double): Long =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val customer = Customer(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                totalDue = initialDue,
                createdAt = now,
                updatedAt = now
            )
            val customerId = customerDao.insertCustomer(customer)
            if (initialDue != 0.0) {
                val txType = if (initialDue > 0) "DUE" else "PAYMENT"
                val absAmount = kotlin.math.abs(initialDue)
                customerTxDao.insertTransaction(
                    CustomerTransaction(
                        customerId = customerId,
                        amount = absAmount,
                        type = txType,
                        note = "পূর্বের জের (Previous balance)",
                        timestamp = now
                    )
                )
            }
            customerId
        }

    suspend fun addCustomerTransaction(
        customerId: Long,
        amount: Double,
        type: String, // "DUE" (বাকি দিলাম) or "PAYMENT" (জমা পেলাম)
        note: String,
        linkToCashBox: Boolean = true
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val customer = customerDao.getCustomerById(customerId) ?: return@withContext
        val newDue = if (type == "DUE") {
            customer.totalDue + amount
        } else {
            customer.totalDue - amount
        }

        customerDao.updateDue(customerId, newDue, now)
        customerTxDao.insertTransaction(
            CustomerTransaction(
                customerId = customerId,
                amount = amount,
                type = type,
                note = note.trim(),
                timestamp = now
            )
        )

        // If customer paid cash, automatically register Cash In
        if (linkToCashBox && type == "PAYMENT") {
            cashEntryDao.insertCashEntry(
                CashEntry(
                    amount = amount,
                    type = "CASH_IN",
                    category = "কাস্টমার জমা",
                    note = "${customer.name} - $note",
                    timestamp = now
                )
            )
        }
    }

    suspend fun deleteCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        customerTxDao.deleteForCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    // Supplier operations
    fun getTransactionsForSupplierFlow(supplierId: Long): Flow<List<SupplierTransaction>> =
        supplierTxDao.getTransactionsForSupplierFlow(supplierId)

    suspend fun addSupplier(name: String, phone: String, company: String, initialDue: Double): Long =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val supplier = Supplier(
                name = name.trim(),
                phone = phone.trim(),
                company = company.trim(),
                totalDue = initialDue,
                createdAt = now,
                updatedAt = now
            )
            val supplierId = supplierDao.insertSupplier(supplier)
            if (initialDue != 0.0) {
                supplierTxDao.insertTransaction(
                    SupplierTransaction(
                        supplierId = supplierId,
                        amount = initialDue,
                        type = "PURCHASE_DUE",
                        note = "পূর্বের দেনা (Previous due)",
                        timestamp = now
                    )
                )
            }
            supplierId
        }

    suspend fun addSupplierTransaction(
        supplierId: Long,
        amount: Double,
        type: String, // "PURCHASE_DUE" (মাল কিনলাম/দেনা হলো) or "PAID" (পরিশোধ করলাম)
        note: String,
        linkToCashBox: Boolean = true
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val supplier = supplierDao.getSupplierById(supplierId) ?: return@withContext
        val newDue = if (type == "PURCHASE_DUE") {
            supplier.totalDue + amount
        } else {
            supplier.totalDue - amount
        }

        supplierDao.updateDue(supplierId, newDue, now)
        supplierTxDao.insertTransaction(
            SupplierTransaction(
                supplierId = supplierId,
                amount = amount,
                type = type,
                note = note.trim(),
                timestamp = now
            )
        )

        // If shopkeeper paid supplier cash, automatically register Cash Out
        if (linkToCashBox && type == "PAID") {
            cashEntryDao.insertCashEntry(
                CashEntry(
                    amount = amount,
                    type = "CASH_OUT",
                    category = "সাপ্লায়ার পেমেন্ট",
                    note = "${supplier.name} - $note",
                    timestamp = now
                )
            )
        }
    }

    suspend fun deleteSupplier(supplier: Supplier) = withContext(Dispatchers.IO) {
        supplierTxDao.deleteForSupplier(supplier.id)
        supplierDao.deleteSupplier(supplier)
    }

    // Cash operations
    suspend fun addCashEntry(amount: Double, type: String, category: String, note: String) =
        withContext(Dispatchers.IO) {
            cashEntryDao.insertCashEntry(
                CashEntry(
                    amount = amount,
                    type = type,
                    category = category,
                    note = note.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }

    suspend fun deleteCashEntry(entry: CashEntry) = withContext(Dispatchers.IO) {
        cashEntryDao.deleteCashEntry(entry)
    }

    // Backup & Cloud sync simulation
    suspend fun performCloudSync(): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val profile = userProfileDao.getProfile() ?: UserProfile()
        userProfileDao.insertOrUpdate(profile.copy(lastBackupTimestamp = now, isCloudSyncEnabled = true))
        now
    }

    // Initial seed data if first time
    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getProfile()
        if (profile == null) {
            // First time setup
            userProfileDao.insertOrUpdate(
                UserProfile(
                    id = 1,
                    shopName = "রবিউল ট্রেডার্স",
                    ownerName = "এমডি রবিউল ইসলাম",
                    phone = "01700000000",
                    email = "rabiul.baishari@gmail.com",
                    passwordHash = "1234",
                    isPasswordSet = false, // Will prompt first-time setup or PIN setup
                    isBiometricEnabled = true,
                    isCloudSyncEnabled = true
                )
            )
            // Seed a couple realistic sample entries for smooth first run
            val now = System.currentTimeMillis()
            val c1 = customerDao.insertCustomer(
                Customer(name = "মোহাম্মদ রফিক", phone = "01819234567", address = "চকবাজার, ঢাকা", totalDue = 1500.0, updatedAt = now, createdAt = now)
            )
            customerTxDao.insertTransaction(
                CustomerTransaction(customerId = c1, amount = 2000.0, type = "DUE", note = "চাল ও ডাল বাকিতে বিক্রি", timestamp = now - 86400000L)
            )
            customerTxDao.insertTransaction(
                CustomerTransaction(customerId = c1, amount = 500.0, type = "PAYMENT", note = "নগদ আংশিক পরিশোধ", timestamp = now)
            )

            val c2 = customerDao.insertCustomer(
                Customer(name = "আনোয়ার হোসেন", phone = "01711987654", address = "নিউ মার্কেট", totalDue = 3200.0, updatedAt = now, createdAt = now)
            )
            customerTxDao.insertTransaction(
                CustomerTransaction(customerId = c2, amount = 3200.0, type = "DUE", note = "তেল ও মশলা ক্রয়", timestamp = now - 172800000L)
            )

            val s1 = supplierDao.insertSupplier(
                Supplier(name = "মেসার্স যমুনা ট্রেডিং", phone = "01912345678", company = "পাইকারি ডিলার", totalDue = 5000.0, updatedAt = now, createdAt = now)
            )
            supplierTxDao.insertTransaction(
                SupplierTransaction(supplierId = s1, amount = 15000.0, type = "PURCHASE_DUE", note = "৫০ বস্তা চাল চালান #১০২", timestamp = now - 259200000L)
            )
            supplierTxDao.insertTransaction(
                SupplierTransaction(supplierId = s1, amount = 10000.0, type = "PAID", note = "নগদ পরিশোধ", timestamp = now - 86400000L)
            )

            // Seed cash
            cashEntryDao.insertCashEntry(
                CashEntry(amount = 12500.0, type = "CASH_IN", category = "নগদ বিক্রি", note = "সকালের নগদ বেচাকেনা", timestamp = now - 14400000L)
            )
            cashEntryDao.insertCashEntry(
                CashEntry(amount = 500.0, type = "CASH_OUT", category = "দোকান খরচ", note = "চা ও নাশতা", timestamp = now - 7200000L)
            )
        }
    }
}
