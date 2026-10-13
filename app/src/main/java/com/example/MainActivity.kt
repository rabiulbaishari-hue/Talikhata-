package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.data.model.UserProfile
import com.example.data.security.BiometricHelper
import com.example.ui.components.AboutDialog
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.viewmodel.AuthScreenState
import com.example.ui.screens.CashboxScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.FirstTimeSetupScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.ReportsAndSettingsScreen
import com.example.ui.screens.SupplierDetailScreen
import com.example.ui.screens.SupplierListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TallyGold
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.viewmodel.TallyViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: TallyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val authState by viewModel.authState.collectAsState()
                val profile by viewModel.profile.collectAsState()
                val customers by viewModel.customers.collectAsState()
                val suppliers by viewModel.suppliers.collectAsState()
                val cashEntries by viewModel.cashEntries.collectAsState()
                val selectedTab by viewModel.selectedTab.collectAsState()
                val selectedCustomer by viewModel.selectedCustomer.collectAsState()
                val selectedSupplier by viewModel.selectedSupplier.collectAsState()
                val customerTransactions by viewModel.customerTransactions.collectAsState()
                val supplierTransactions by viewModel.supplierTransactions.collectAsState()
                val forgotPasswordVisible by viewModel.forgotPasswordDialogVisible.collectAsState()
                val showAboutDialog by viewModel.showAboutDialog.collectAsState()
                val showWelcomeBanner by viewModel.showWelcomeBanner.collectAsState()
                val message by viewModel.message.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(message) {
                    message?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.setMessage(null)
                    }
                }

                // If locked and biometric is available, auto-trigger biometric prompt on startup once
                LaunchedEffect(authState) {
                    if (authState is AuthScreenState.Locked && profile?.isBiometricEnabled == true) {
                        if (BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                            BiometricHelper.promptBiometric(
                                activity = this@MainActivity,
                                title = "ফেস বা ফিঙ্গারপ্রিন্ট লগইন",
                                subtitle = "টালিখাতা অ্যাকাউন্টে প্রবেশ করতে বায়োমেট্রিক স্ক্যান করুন",
                                negativeButtonText = "পাসওয়ার্ড ব্যবহার করুন",
                                onSuccess = { viewModel.unlockViaBiometric() },
                                onError = { /* User can still use password */ }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (authState) {
                        is AuthScreenState.FirstTimeSetup -> {
                            FirstTimeSetupScreen(
                                onCompleteSetup = { shopName, ownerName, phone, email, password, enableBiometric ->
                                    viewModel.completeFirstTimeSetup(
                                        shopName,
                                        ownerName,
                                        phone,
                                        email,
                                        password,
                                        enableBiometric
                                    )
                                },
                                onShowAbout = { viewModel.setShowAboutDialog(true) }
                            )
                        }

                        is AuthScreenState.Locked -> {
                            LockScreen(
                                profile = profile,
                                onUnlockWithPassword = { viewModel.verifyPassword(it) },
                                onTriggerBiometric = {
                                    BiometricHelper.promptBiometric(
                                        activity = this@MainActivity,
                                        title = "ফেস বা ফিঙ্গারপ্রিন্ট লগইন",
                                        subtitle = "টালিখাতা সুরক্ষিত অ্যাকাউন্টে প্রবেশ করুন",
                                        negativeButtonText = "পাসওয়ার্ড দিন",
                                        onSuccess = { viewModel.unlockViaBiometric() },
                                        onError = { viewModel.setMessage(it) }
                                    )
                                },
                                onForgotPasswordClick = { viewModel.openForgotPasswordDialog() },
                                onShowAbout = { viewModel.setShowAboutDialog(true) }
                            )
                        }

                        is AuthScreenState.Unlocked -> {
                            if (selectedCustomer != null) {
                                CustomerDetailScreen(
                                    customer = selectedCustomer!!,
                                    transactions = customerTransactions,
                                    shopName = profile?.shopName ?: "রবিউল ট্রেডার্স",
                                    onBack = { viewModel.clearSelectedCustomer() },
                                    onAddTransaction = { customerId, amount, type, note, linkToCash ->
                                        viewModel.addCustomerTransaction(customerId, amount, type, note, linkToCash)
                                    },
                                    onDeleteCustomer = { viewModel.deleteCustomer(it) },
                                    onExportPdf = { viewModel.exportCustomerStatementPdf(it) }
                                )
                            } else if (selectedSupplier != null) {
                                SupplierDetailScreen(
                                    supplier = selectedSupplier!!,
                                    transactions = supplierTransactions,
                                    onBack = { viewModel.clearSelectedSupplier() },
                                    onAddTransaction = { supplierId, amount, type, note, linkToCash ->
                                        viewModel.addSupplierTransaction(supplierId, amount, type, note, linkToCash)
                                    },
                                    onDeleteSupplier = { viewModel.deleteSupplier(it) }
                                )
                            } else {
                                MainAppShell(
                                    profile = profile ?: UserProfile(),
                                    selectedTab = selectedTab,
                                    onSelectTab = { viewModel.setSelectedTab(it) },
                                    onLockApp = { viewModel.lockApp() },
                                    onShowAbout = { viewModel.setShowAboutDialog(true) },
                                    onSyncCloud = { viewModel.triggerCloudSync() },
                                    content = {
                                        when (selectedTab) {
                                            0 -> CustomerScreen(
                                                customers = customers,
                                                shopName = profile?.shopName ?: "রবিউল ট্রেডার্স",
                                                onSelectCustomer = { viewModel.selectCustomer(it) },
                                                onAddCustomer = { name, phone, address, initialDue ->
                                                    viewModel.addCustomer(name, phone, address, initialDue)
                                                }
                                            )
                                            1 -> SupplierListScreen(
                                                suppliers = suppliers,
                                                onSelectSupplier = { viewModel.selectSupplier(it) },
                                                onAddSupplier = { name, phone, company, initialDue ->
                                                    viewModel.addSupplier(name, phone, company, initialDue)
                                                }
                                            )
                                            2 -> CashboxScreen(
                                                cashEntries = cashEntries,
                                                onAddCashEntry = { amount, type, category, note ->
                                                    viewModel.addCashEntry(amount, type, category, note)
                                                },
                                                onDeleteCashEntry = { viewModel.deleteCashEntry(it) }
                                            )
                                            3 -> ReportsAndSettingsScreen(
                                                profile = profile,
                                                customers = customers,
                                                suppliers = suppliers,
                                                cashEntries = cashEntries,
                                                onTriggerCloudSync = { viewModel.triggerCloudSync() },
                                                onToggleBiometric = { viewModel.toggleBiometricSetting(it) },
                                                onLockApp = { viewModel.lockApp() },
                                                onShowAboutDialog = { viewModel.setShowAboutDialog(true) }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Welcome Banner Toast / Alert
                    AnimatedVisibility(
                        visible = showWelcomeBanner,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TallyPrimary),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "স্বাগতম টালিখাতা অ্যাপস",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "ক্রিয়েট বাই এমডি রবিউল ইসলাম (rabiul.baishari@gmail.com)",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.dismissWelcomeBanner() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                }
                            }
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    // Dialogs
                    if (showAboutDialog) {
                        AboutDialog(
                            profile = profile ?: UserProfile(),
                            onDismiss = { viewModel.setShowAboutDialog(false) }
                        )
                    }

                    if (forgotPasswordVisible) {
                        ForgotPasswordDialog(
                            savedPhone = profile?.phone ?: "",
                            onSendOtp = { viewModel.sendVerificationCode(it) },
                            onVerifyOtp = { viewModel.verifyOtp(it) },
                            onResetPassword = { viewModel.resetPasswordWithOtp(it) },
                            onDismiss = { viewModel.closeForgotPasswordDialog() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
    profile: UserProfile,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onLockApp: () -> Unit,
    onShowAbout: () -> Unit,
    onSyncCloud: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                profile.shopName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            "${profile.ownerName} | টালিখাতা",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.5.sp
                        )
                    }
                },
                actions = {
                    // Cloud Status Pill / Button
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable(onClick = onSyncCloud)
                            .testTag("top_cloud_sync_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = "Cloud",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ক্লাউড", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // About Icon (Explicitly requested by user)
                    IconButton(
                        onClick = onShowAbout,
                        modifier = Modifier.testTag("top_about_icon_btn")
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "About Creator",
                            tint = Color.White
                        )
                    }

                    // Lock Button
                    IconButton(
                        onClick = onLockApp,
                        modifier = Modifier.testTag("top_lock_app_btn")
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Lock App",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TallyPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Tab 0: গ্রাহক (Customers)
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onSelectTab(0) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Customers") },
                    label = { Text("গ্রাহক", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TallyPrimary,
                        selectedTextColor = TallyPrimary,
                        indicatorColor = TallyPrimaryLight
                    ),
                    modifier = Modifier.testTag("nav_tab_customers")
                )

                // Tab 1: মহাজন (Suppliers)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onSelectTab(1) },
                    icon = { Icon(Icons.Default.Store, contentDescription = "Suppliers") },
                    label = { Text("মহাজন", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TallyPrimary,
                        selectedTextColor = TallyPrimary,
                        indicatorColor = TallyPrimaryLight
                    ),
                    modifier = Modifier.testTag("nav_tab_suppliers")
                )

                // Tab 2: ক্যাশবাক্স (Cashbox)
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onSelectTab(2) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Cashbox") },
                    label = { Text("ক্যাশবাক্স", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TallyPrimary,
                        selectedTextColor = TallyPrimary,
                        indicatorColor = TallyPrimaryLight
                    ),
                    modifier = Modifier.testTag("nav_tab_cashbox")
                )

                // Tab 3: রিপোর্ট ও ক্লাউড (Reports & Cloud)
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onSelectTab(3) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                    label = { Text("রিপোর্ট", fontSize = 12.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TallyPrimary,
                        selectedTextColor = TallyPrimary,
                        indicatorColor = TallyPrimaryLight
                    ),
                    modifier = Modifier.testTag("nav_tab_reports")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}
