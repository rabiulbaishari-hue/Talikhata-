package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Supplier
import com.example.data.model.SupplierTransaction
import com.example.ui.components.AddSupplierDialog
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyGreenIncomeLight
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.theme.TallyRedDue
import com.example.ui.theme.TallyRedDueLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupplierListScreen(
    suppliers: List<Supplier>,
    onSelectSupplier: (Supplier) -> Unit,
    onAddSupplier: (name: String, phone: String, company: String, initialDue: Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredSuppliers = suppliers.filter { supp ->
        supp.name.contains(searchQuery, ignoreCase = true) ||
                supp.company.contains(searchQuery, ignoreCase = true) ||
                supp.phone.contains(searchQuery)
    }

    val totalPayable = suppliers.sumOf { it.totalDue }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("মোট মহাজন দেনা (তারা পাবে)", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳ %.2f".format(totalPayable),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TallyRedDue
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("মোট মহাজন", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${suppliers.size} জন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TallyPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supplier_search_bar"),
                placeholder = { Text("মহাজন বা কোম্পানির নাম দিয়ে খুঁজুন") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Supplier List
            if (filteredSuppliers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (searchQuery.isNotEmpty()) "কোনো মহাজন পাওয়া যায়নি"
                            else "কোনো মহাজন যোগ করা হয়নি",
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("নতুন মহাজন যুক্ত করতে '+' বাটনে চাপুন", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSuppliers, key = { it.id }) { supplier ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSupplier(supplier) }
                                .testTag("supplier_card_${supplier.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(TallyPrimaryLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        supplier.name.firstOrNull()?.toString() ?: "S",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = TallyPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(supplier.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    if (supplier.company.isNotEmpty()) {
                                        Text(supplier.company, fontSize = 12.5.sp, color = Color.Gray)
                                    }
                                    Text(supplier.phone.ifEmpty { "ফোন নম্বর নেই" }, fontSize = 11.5.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = if (supplier.totalDue > 0) TallyRedDueLight else TallyGreenIncomeLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "দেনা ৳ %.2f".format(supplier.totalDue),
                                            color = if (supplier.totalDue > 0) TallyRedDue else TallyGreenIncome,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    if (supplier.phone.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${supplier.phone}")
                                                }
                                                context.startActivity(dialIntent)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = "Call", tint = TallyPrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_supplier_fab"),
            containerColor = TallyPrimary,
            contentColor = Color.White
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add Supplier")
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন মহাজন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showAddDialog) {
        AddSupplierDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, company, initialDue ->
                onAddSupplier(name, phone, company, initialDue)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDetailScreen(
    supplier: Supplier,
    transactions: List<SupplierTransaction>,
    onBack: () -> Unit,
    onAddTransaction: (supplierId: Long, amount: Double, type: String, note: String, linkToCash: Boolean) -> Unit,
    onDeleteSupplier: (Supplier) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    var showTxDialog by remember { mutableStateOf(false) }
    var isPurchaseDue by remember { mutableStateOf(true) } // true: মাল কিনলাম (দেনা বাড়ল), false: পরিশোধ করলাম (দেনা কমল)
    var txAmountText by remember { mutableStateOf("") }
    var txNote by remember { mutableStateOf("") }
    var linkToCash by remember { mutableStateOf(true) }
    var txError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(supplier.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(supplier.company.ifEmpty { "মহাজন খাতা" }, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TallyPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // মাল কিনলাম / দেনা হলো (Red)
                    Button(
                        onClick = {
                            isPurchaseDue = true
                            showTxDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("supplier_buy_due_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyRedDue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ মাল কিনলাম (দেনা)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // পরিশোধ করলাম (Green)
                    Button(
                        onClick = {
                            isPurchaseDue = false
                            showTxDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("supplier_pay_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyGreenIncome),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("- পরিশোধ করলাম", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Balance Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = TallyRedDueLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("মোট মহাজন দেনা (তারা পাবে)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TallyRedDue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("৳ %.2f".format(supplier.totalDue), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TallyRedDue)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (supplier.phone.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${supplier.phone}")
                                }
                                context.startActivity(dialIntent)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("কল করুন", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("মহাজন লেনদেন (${transactions.size} টি)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(6.dp))

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("কোনো লেনদেন রেকর্ড নেই", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        val isDue = tx.type == "PURCHASE_DUE"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (isDue) "মাল কেনা হয়েছে (বাকি)" else "টাকা পরিশোধ করা হয়েছে",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDue) TallyRedDue else TallyGreenIncome
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(tx.note.ifEmpty { "লেনদেন" }, fontSize = 12.5.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(sdf.format(Date(tx.timestamp)), fontSize = 11.sp, color = Color.Gray)
                                }

                                Text(
                                    "৳ %.2f".format(tx.amount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDue) TallyRedDue else TallyGreenIncome
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showTxDialog) {
        val dialogTitle = if (isPurchaseDue) "মাল কিনলাম (দেনা হলো)" else "টাকা পরিশোধ করলাম"
        val dialogColor = if (isPurchaseDue) TallyRedDue else TallyGreenIncome

        AlertDialog(
            onDismissRequest = { showTxDialog = false },
            title = { Text(dialogTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = dialogColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("মহাজন: ${supplier.name}", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = txAmountText,
                        onValueChange = { txAmountText = it; txError = null },
                        label = { Text("টাকার পরিমাণ (৳) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("supplier_tx_amount_input")
                    )
                    OutlinedTextField(
                        value = txNote,
                        onValueChange = { txNote = it },
                        label = { Text("বিবরণ / চালানের বিবরণ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("supplier_tx_note_input")
                    )
                    if (txError != null) {
                        Text(txError ?: "", color = TallyRedDue, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = txAmountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            txError = "সঠিক পরিমাণ লিখুন"
                            return@Button
                        }
                        val type = if (isPurchaseDue) "PURCHASE_DUE" else "PAID"
                        onAddTransaction(supplier.id, amount, type, txNote.trim(), linkToCash)
                        showTxDialog = false
                        txAmountText = ""
                        txNote = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = dialogColor),
                    modifier = Modifier.testTag("confirm_supplier_tx_btn")
                ) {
                    Text("নিশ্চিত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTxDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("মহাজন মুছে ফেলবেন?") },
            text = { Text("${supplier.name}-এর সকল তথ্য ও লেনদেন মুছে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteSupplier(supplier)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TallyRedDue)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
