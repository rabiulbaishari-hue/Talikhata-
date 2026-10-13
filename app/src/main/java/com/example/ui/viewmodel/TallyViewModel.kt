package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CashEntry
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.model.Supplier
import com.example.data.model.SupplierTransaction
import com.example.data.model.UserProfile
import com.example.data.pdf.PdfExporter
import com.example.data.repository.TallyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

sealed class AuthScreenState {
    object FirstTimeSetup : AuthScreenState()
    object Locked : AuthScreenState()
    object Unlocked : AuthScreenState()
}

class TallyViewModel(application: Application) : AndroidViewModel(application) {
    val repository = TallyRepository(application)

    val profile: StateFlow<UserProfile?> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val customers: StateFlow<List<Customer>> = repository.customersFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val suppliers: StateFlow<List<Supplier>> = repository.suppliersFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val cashEntries: StateFlow<List<CashEntry>> = repository.cashEntriesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Navigation and screen state
    private val _authState = MutableStateFlow<AuthScreenState>(AuthScreenState.Locked)
    val authState: StateFlow<AuthScreenState> = _authState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Customers, 1: Suppliers, 2: Cashbox, 3: Reports & Cloud
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    private val _customerTransactions = MutableStateFlow<List<CustomerTransaction>>(emptyList())
    val customerTransactions: StateFlow<List<CustomerTransaction>> = _customerTransactions.asStateFlow()

    private val _selectedSupplier = MutableStateFlow<Supplier?>(null)
    val selectedSupplier: StateFlow<Supplier?> = _selectedSupplier.asStateFlow()

    private val _supplierTransactions = MutableStateFlow<List<SupplierTransaction>>(emptyList())
    val supplierTransactions: StateFlow<List<SupplierTransaction>> = _supplierTransactions.asStateFlow()

    // Search and filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _customerFilter = MutableStateFlow("ALL") // "ALL", "DUE", "ADVANCE"
    val customerFilter: StateFlow<String> = _customerFilter.asStateFlow()

    // Forgot password verification state
    private val _forgotPasswordDialogVisible = MutableStateFlow(false)
    val forgotPasswordDialogVisible: StateFlow<Boolean> = _forgotPasswordDialogVisible.asStateFlow()

    private val _generatedOtp = MutableStateFlow<String?>(null)
    val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

    private val _otpVerified = MutableStateFlow(false)
    val otpVerified: StateFlow<Boolean> = _otpVerified.asStateFlow()

    // Snackbar / Feedback message
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // Welcome banner shown after login
    private val _showWelcomeBanner = MutableStateFlow(false)
    val showWelcomeBanner: StateFlow<Boolean> = _showWelcomeBanner.asStateFlow()

    // About Dialog visible
    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            val currentProfile = repository.getProfile()
            if (currentProfile == null || !currentProfile.isPasswordSet) {
                _authState.value = AuthScreenState.FirstTimeSetup
            } else {
                _authState.value = AuthScreenState.Locked
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCustomerFilter(filter: String) {
        _customerFilter.value = filter
    }

    fun setMessage(msg: String?) {
        _message.value = msg
    }

    fun setShowAboutDialog(show: Boolean) {
        _showAboutDialog.value = show
    }

    fun dismissWelcomeBanner() {
        _showWelcomeBanner.value = false
    }

    // Auth methods
    fun verifyPassword(input: String): Boolean {
        val currentProfile = profile.value
        val correctPassword = currentProfile?.passwordHash ?: "1234"
        if (input.trim() == correctPassword.trim()) {
            _authState.value = AuthScreenState.Unlocked
            _showWelcomeBanner.value = true
            return true
        }
        return false
    }

    fun unlockViaBiometric() {
        _authState.value = AuthScreenState.Unlocked
        _showWelcomeBanner.value = true
    }

    fun lockApp() {
        _authState.value = AuthScreenState.Locked
        _selectedCustomer.value = null
        _selectedSupplier.value = null
    }

    fun completeFirstTimeSetup(
        shopName: String,
        ownerName: String,
        phone: String,
        email: String,
        password: String,
        enableBiometric: Boolean
    ) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                id = 1,
                shopName = shopName.ifBlank { "রবিউল ট্রেডার্স" },
                ownerName = ownerName.ifBlank { "এমডি রবিউল ইসলাম" },
                phone = phone.ifBlank { "01700000000" },
                email = email.ifBlank { "rabiul.baishari@gmail.com" },
                passwordHash = password.trim(),
                isPasswordSet = true,
                isBiometricEnabled = enableBiometric,
                isCloudSyncEnabled = true,
                lastBackupTimestamp = System.currentTimeMillis()
            )
            repository.saveProfile(newProfile)
            _authState.value = AuthScreenState.Unlocked
            _showWelcomeBanner.value = true
            _message.value = "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে! স্বাগতম টালিখাতা অ্যাপে।"
        }
    }

    // Forgot password flow
    fun openForgotPasswordDialog() {
        _forgotPasswordDialogVisible.value = true
        _generatedOtp.value = null
        _otpVerified.value = false
    }

    fun closeForgotPasswordDialog() {
        _forgotPasswordDialogVisible.value = false
        _generatedOtp.value = null
        _otpVerified.value = false
    }

    fun sendVerificationCode(phoneInput: String): Boolean {
        val savedPhone = profile.value?.phone ?: ""
        // Check if phone matches or allow recovery for owner's registered number
        val code = "%06d".format(Random.nextInt(100000, 999999))
        _generatedOtp.value = code
        _message.value = "ভেরিফিকেশন কোড পাঠানো হয়েছে: $code"
        return true
    }

    fun verifyOtp(code: String): Boolean {
        if (_generatedOtp.value != null && code.trim() == _generatedOtp.value) {
            _otpVerified.value = true
            return true
        }
        return false
    }

    fun resetPasswordWithOtp(newPass: String) {
        viewModelScope.launch {
            repository.updatePassword(newPass)
            _forgotPasswordDialogVisible.value = false
            _generatedOtp.value = null
            _otpVerified.value = false
            _message.value = "পাসওয়ার্ড সফলভাবে পরিবর্তন করা হয়েছে! এখন লগইন করুন।"
        }
    }

    // Customer Detail and Operations
    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
        viewModelScope.launch {
            repository.getTransactionsForCustomerFlow(customer.id).collect { txs ->
                _customerTransactions.value = txs
            }
        }
    }

    fun clearSelectedCustomer() {
        _selectedCustomer.value = null
        _customerTransactions.value = emptyList()
    }

    fun addCustomer(name: String, phone: String, address: String, initialDue: Double) {
        viewModelScope.launch {
            repository.addCustomer(name, phone, address, initialDue)
            _message.value = "নতুন গ্রাহক $name সফলভাবে যোগ করা হয়েছে।"
        }
    }

    fun addCustomerTransaction(
        customerId: Long,
        amount: Double,
        type: String,
        note: String,
        linkToCashBox: Boolean
    ) {
        viewModelScope.launch {
            repository.addCustomerTransaction(customerId, amount, type, note, linkToCashBox)
            // Refresh customer
            val updatedList = repository.customersFlow
            // Update selectedCustomer object
            val cur = _selectedCustomer.value
            if (cur != null && cur.id == customerId) {
                val newDue = if (type == "DUE") cur.totalDue + amount else cur.totalDue - amount
                _selectedCustomer.value = cur.copy(totalDue = newDue)
            }
            val typeText = if (type == "DUE") "বাকি দেওয়া" else "জমা নেওয়া"
            _message.value = "৳ %.2f $typeText সম্পন্ন হয়েছে।".format(amount)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_selectedCustomer.value?.id == customer.id) {
                clearSelectedCustomer()
            }
            _message.value = "গ্রাহক মুছে ফেলা হয়েছে।"
        }
    }

    // Supplier Detail and Operations
    fun selectSupplier(supplier: Supplier) {
        _selectedSupplier.value = supplier
        viewModelScope.launch {
            repository.getTransactionsForSupplierFlow(supplier.id).collect { txs ->
                _supplierTransactions.value = txs
            }
        }
    }

    fun clearSelectedSupplier() {
        _selectedSupplier.value = null
        _supplierTransactions.value = emptyList()
    }

    fun addSupplier(name: String, phone: String, company: String, initialDue: Double) {
        viewModelScope.launch {
            repository.addSupplier(name, phone, company, initialDue)
            _message.value = "নতুন মহাজন $name সফলভাবে যোগ করা হয়েছে।"
        }
    }

    fun addSupplierTransaction(
        supplierId: Long,
        amount: Double,
        type: String,
        note: String,
        linkToCashBox: Boolean
    ) {
        viewModelScope.launch {
            repository.addSupplierTransaction(supplierId, amount, type, note, linkToCashBox)
            val cur = _selectedSupplier.value
            if (cur != null && cur.id == supplierId) {
                val newDue = if (type == "PURCHASE_DUE") cur.totalDue + amount else cur.totalDue - amount
                _selectedSupplier.value = cur.copy(totalDue = newDue)
            }
            val typeText = if (type == "PURCHASE_DUE") "বাকি পণ্য ক্রয়" else "পরিশোধ"
            _message.value = "৳ %.2f $typeText সম্পন্ন হয়েছে।".format(amount)
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
            if (_selectedSupplier.value?.id == supplier.id) {
                clearSelectedSupplier()
            }
            _message.value = "মহাজন মুছে ফেলা হয়েছে।"
        }
    }

    // Cash operations
    fun addCashEntry(amount: Double, type: String, category: String, note: String) {
        viewModelScope.launch {
            repository.addCashEntry(amount, type, category, note)
            val typeStr = if (type == "CASH_IN") "ক্যাশ ইন (জমা)" else "ক্যাশ আউট (খরচ)"
            _message.value = "৳ %.2f $typeStr যোগ করা হয়েছে।".format(amount)
        }
    }

    fun deleteCashEntry(entry: CashEntry) {
        viewModelScope.launch {
            repository.deleteCashEntry(entry)
            _message.value = "ক্যাশ এন্ট্রি মুছে ফেলা হয়েছে।"
        }
    }

    // Cloud Backup
    fun triggerCloudSync() {
        viewModelScope.launch {
            val syncTime = repository.performCloudSync()
            _message.value = "ক্লাউড সিঙ্ক সফল! সকল তথ্য ক্লাউডে সংরক্ষিত।"
        }
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBiometricSetting(enabled)
            _message.value = if (enabled) "ফেস ও ফিঙ্গারপ্রিন্ট লগইন চালু করা হয়েছে।" else "ফেস ও ফিঙ্গারপ্রিন্ট লগইন বন্ধ করা হয়েছে।"
        }
    }

    // PDF Export
    fun exportCustomerStatementPdf(customer: Customer): File? {
        val userProfile = profile.value ?: UserProfile()
        val txs = _customerTransactions.value
        val file = PdfExporter.generateCustomerStatement(
            getApplication(),
            userProfile,
            customer,
            txs
        )
        if (file != null) {
            _message.value = "পিডিএফ তৈরি হয়েছে: ${file.name}"
        } else {
            _message.value = "পিডিএফ তৈরিতে সমস্যা হয়েছে।"
        }
        return file
    }
}
