package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.pdf.PdfExporter
import com.example.ui.components.AddCustomerTransactionDialog
import com.example.ui.components.PaymentReminderDialog
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyGreenIncomeLight
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.theme.TallyRedDue
import com.example.ui.theme.TallyRedDueLight
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customer: Customer,
    transactions: List<CustomerTransaction>,
    shopName: String,
    onBack: () -> Unit,
    onAddTransaction: (customerId: Long, amount: Double, type: String, note: String, linkToCash: Boolean) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    onExportPdf: (Customer) -> File?
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    var showTransactionDialog by remember { mutableStateOf(false) }
    var isTransactionDue by remember { mutableStateOf(true) } // true: বাকি দিলাম, false: জমা পেলাম
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(customer.phone.ifEmpty { "গ্রাহক লেজার" }, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // PDF Export Button
                    IconButton(
                        onClick = {
                            val pdfFile = onExportPdf(customer)
                            if (pdfFile != null) {
                                PdfExporter.sharePdf(context, pdfFile, "${customer.name} - হিসাব বিবরণী")
                            }
                        },
                        modifier = Modifier.testTag("export_customer_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Export", tint = Color.White)
                    }

                    // Delete Customer
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
            // Dual Action Bottom Buttons: বাকি দিলাম & জমা পেলাম
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
                    // বাকি দিলাম (Red)
                    Button(
                        onClick = {
                            isTransactionDue = true
                            showTransactionDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("customer_give_due_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyRedDue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("- বাকি দিলাম", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    // জমা পেলাম (Green)
                    Button(
                        onClick = {
                            isTransactionDue = false
                            showTransactionDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("customer_receive_pay_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyGreenIncome),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ জমা পেলাম", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (customer.totalDue >= 0) TallyRedDueLight else TallyGreenIncomeLight
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (customer.totalDue >= 0) "মোট বাকি পাওনা" else "মোট অগ্রিম জমা",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (customer.totalDue >= 0) TallyRedDue else TallyGreenIncome
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "৳ %.2f".format(kotlin.math.abs(customer.totalDue)),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (customer.totalDue >= 0) TallyRedDue else TallyGreenIncome
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons inside card (Call & Taagada)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (customer.phone.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${customer.phone}")
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

                        if (customer.totalDue > 0) {
                            Button(
                                onClick = { showReminderDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("তাগাদা দিন", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PDF Banner Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TallyPrimaryLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TallyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("হিসাব বিবরণী (PDF)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TallyPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            val pdfFile = onExportPdf(customer)
                            if (pdfFile != null) {
                                PdfExporter.sharePdf(context, pdfFile, "${customer.name} - হিসাব বিবরণী")
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("শেয়ার PDF", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "লেনদেনের ইতিহাস (${transactions.size} টি)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "কোনো লেনদেন রেকর্ড নেই\nনিচের বাটন দিয়ে লেনদেন যুক্ত করুন",
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        val isDue = tx.type == "DUE"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (isDue) "বাকি দেওয়া হয়েছে" else "নগদ আদায় / জমা",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDue) TallyRedDue else TallyGreenIncome
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        tx.note.ifEmpty { "কোনো বিবরণ নেই" },
                                        fontSize = 12.5.sp,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        sdf.format(Date(tx.timestamp)),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    "${if (isDue) "-" else "+"} ৳ %.2f".format(tx.amount),
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

    if (showTransactionDialog) {
        AddCustomerTransactionDialog(
            customer = customer,
            isDue = isTransactionDue,
            onDismiss = { showTransactionDialog = false },
            onConfirm = { amount, type, note, linkToCash ->
                onAddTransaction(customer.id, amount, type, note, linkToCash)
            }
        )
    }

    if (showReminderDialog) {
        PaymentReminderDialog(
            customer = customer,
            shopName = shopName,
            onDismiss = { showReminderDialog = false }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("গ্রাহক মুছে ফেলবেন?") },
            text = { Text("${customer.name}-এর সকল লেনদেন ও তথ্য স্থায়ীভাবে মুছে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteCustomer(customer)
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
