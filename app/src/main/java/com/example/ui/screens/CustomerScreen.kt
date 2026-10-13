package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.components.AddCustomerDialog
import com.example.ui.components.PaymentReminderDialog
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyGreenIncomeLight
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.theme.TallyRedDue
import com.example.ui.theme.TallyRedDueLight

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    shopName: String,
    onSelectCustomer: (Customer) -> Unit,
    onAddCustomer: (name: String, phone: String, address: String, initialDue: Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL, DUE, ADVANCE
    var showAddDialog by remember { mutableStateOf(false) }
    var reminderCustomer by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = customers.filter { cust ->
        val matchesSearch = cust.name.contains(searchQuery, ignoreCase = true) ||
                cust.phone.contains(searchQuery)
        val matchesFilter = when (filterType) {
            "DUE" -> cust.totalDue > 0
            "ADVANCE" -> cust.totalDue < 0
            else -> true
        }
        matchesSearch && matchesFilter
    }

    val totalDueSum = customers.filter { it.totalDue > 0 }.sumOf { it.totalDue }
    val totalAdvanceSum = customers.filter { it.totalDue < 0 }.sumOf { kotlin.math.abs(it.totalDue) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Summary Dashboard Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total Due
                    Column(modifier = Modifier.weight(1f)) {
                        Text("মোট বাকি পাওনা", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳ %.2f".format(totalDueSum),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TallyRedDue
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color.LightGray)
                    )

                    // Total Advance
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text("মোট অগ্রিম জমা", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳ %.2f".format(totalAdvanceSum),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TallyGreenIncome
                        )
                    }

                    // Count
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text("মোট গ্রাহক", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${customers.size} জন",
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
                    .testTag("customer_search_bar"),
                placeholder = { Text("গ্রাহকের নাম বা মোবাইল নম্বর দিয়ে খুঁজুন") },
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

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { filterType = "ALL" },
                    label = { Text("সকল গ্রাহক (${customers.size})", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = filterType == "DUE",
                    onClick = { filterType = "DUE" },
                    label = { Text("বাকি আছে (${customers.count { it.totalDue > 0 }})", fontSize = 12.sp, color = TallyRedDue) }
                )
                FilterChip(
                    selected = filterType == "ADVANCE",
                    onClick = { filterType = "ADVANCE" },
                    label = { Text("জমা আছে (${customers.count { it.totalDue < 0 }})", fontSize = 12.sp, color = TallyGreenIncome) }
                )
            }

            // Customer List
            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (searchQuery.isNotEmpty()) "কোনো গ্রাহক পাওয়া যায়নি"
                            else "কোনো গ্রাহক যোগ করা হয়নি",
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "নতুন গ্রাহক যোগ করতে নিচের '+' বাটনে চাপুন",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        CustomerCard(
                            customer = customer,
                            onCardClick = { onSelectCustomer(customer) },
                            onCallClick = {
                                if (customer.phone.isNotBlank()) {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${customer.phone}")
                                    }
                                    context.startActivity(dialIntent)
                                }
                            },
                            onReminderClick = {
                                reminderCustomer = customer
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_customer_fab"),
            containerColor = TallyPrimary,
            contentColor = Color.White
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন গ্রাহক", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, address, initialDue ->
                onAddCustomer(name, phone, address, initialDue)
            }
        )
    }

    if (reminderCustomer != null) {
        PaymentReminderDialog(
            customer = reminderCustomer!!,
            shopName = shopName,
            onDismiss = { reminderCustomer = null }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onCardClick: () -> Unit,
    onCallClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("customer_card_${customer.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Customer Initials Avatar
            val initials = customer.name.firstOrNull()?.toString() ?: "C"
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(TallyPrimaryLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TallyPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    customer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    customer.phone.ifEmpty { "ফোন নম্বর নেই" },
                    fontSize = 12.5.sp,
                    color = Color.Gray
                )
                if (customer.address.isNotEmpty()) {
                    Text(
                        customer.address,
                        fontSize = 11.5.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            // Due Status & Quick Actions
            Column(horizontalAlignment = Alignment.End) {
                if (customer.totalDue > 0) {
                    Surface(
                        color = TallyRedDueLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "পাবো ৳ %.2f".format(customer.totalDue),
                            color = TallyRedDue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (customer.totalDue < 0) {
                    Surface(
                        color = TallyGreenIncomeLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "জমা ৳ %.2f".format(kotlin.math.abs(customer.totalDue)),
                            color = TallyGreenIncome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "পরিশোধ",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    if (customer.totalDue > 0) {
                        IconButton(
                            onClick = onReminderClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = "Reminder",
                                tint = TallyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (customer.phone.isNotBlank()) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call",
                                tint = TallyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
