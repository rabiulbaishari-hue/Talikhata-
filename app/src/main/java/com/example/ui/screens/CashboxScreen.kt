package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CashEntry
import com.example.ui.components.AddCashEntryDialog
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
fun CashboxScreen(
    cashEntries: List<CashEntry>,
    onAddCashEntry: (amount: Double, type: String, category: String, note: String) -> Unit,
    onDeleteCashEntry: (CashEntry) -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }
    var isCashInAction by remember { mutableStateOf(true) }

    val totalCashIn = cashEntries.filter { it.type == "CASH_IN" }.sumOf { it.amount }
    val totalCashOut = cashEntries.filter { it.type == "CASH_OUT" }.sumOf { it.amount }
    val netCashInHand = totalCashIn - totalCashOut

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main Cashbox Header Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (netCashInHand >= 0) TallyGreenIncomeLight else TallyRedDueLight
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ক্যাশবাক্স ব্যালেন্স (হাতে নগদ)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = if (netCashInHand >= 0) TallyGreenIncome else TallyRedDue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "৳ %.2f".format(netCashInHand),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (netCashInHand >= 0) TallyGreenIncome else TallyRedDue
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Cash In & Cash Out breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Total Cash In
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(TallyGreenIncome.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = TallyGreenIncome, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("মোট ক্যাশ ইন", fontSize = 11.5.sp, color = Color.Gray)
                            Text("৳ %.2f".format(totalCashIn), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TallyGreenIncome)
                        }
                    }

                    // Total Cash Out
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(TallyRedDue.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = TallyRedDue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("মোট ক্যাশ আউট", fontSize = 11.5.sp, color = Color.Gray)
                            Text("৳ %.2f".format(totalCashOut), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TallyRedDue)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dual Quick Buttons: + ক্যাশ ইন & - ক্যাশ আউট
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    isCashInAction = true
                    showDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("cash_in_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = TallyGreenIncome),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ক্যাশ ইন (জমা)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    isCashInAction = false
                    showDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("cash_out_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = TallyRedDue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ক্যাশ আউট (খরচ)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "ক্যাশ লেনদেনের ইতিহাস (${cashEntries.size} টি)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (cashEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("কোনো ক্যাশ এন্ট্রি নেই\nউপরের বাটন দিয়ে ক্যাশ যোগ করুন", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cashEntries, key = { it.id }) { entry ->
                    val isCashIn = entry.type == "CASH_IN"
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            if (isCashIn) TallyGreenIncomeLight else TallyRedDueLight,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (isCashIn) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (isCashIn) TallyGreenIncome else TallyRedDue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(entry.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (entry.note.isNotEmpty()) {
                                        Text(entry.note, fontSize = 12.sp, color = Color.DarkGray)
                                    }
                                    Text(sdf.format(Date(entry.timestamp)), fontSize = 10.5.sp, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${if (isCashIn) "+" else "-"} ৳ %.2f".format(entry.amount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isCashIn) TallyGreenIncome else TallyRedDue
                                )

                                IconButton(
                                    onClick = { onDeleteCashEntry(entry) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(16.dp))
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

    if (showDialog) {
        AddCashEntryDialog(
            isCashIn = isCashInAction,
            onDismiss = { showDialog = false },
            onConfirm = { amount, type, category, note ->
                onAddCashEntry(amount, type, category, note)
            }
        )
    }
}
