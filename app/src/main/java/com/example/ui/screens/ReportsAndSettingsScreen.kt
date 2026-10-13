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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CashEntry
import com.example.data.model.Customer
import com.example.data.model.Supplier
import com.example.data.model.UserProfile
import com.example.ui.theme.TallyGold
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
fun ReportsAndSettingsScreen(
    profile: UserProfile?,
    customers: List<Customer>,
    suppliers: List<Supplier>,
    cashEntries: List<CashEntry>,
    onTriggerCloudSync: () -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onLockApp: () -> Unit,
    onShowAboutDialog: () -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    val totalCustomerDue = customers.filter { it.totalDue > 0 }.sumOf { it.totalDue }
    val totalSupplierDue = suppliers.sumOf { it.totalDue }
    val totalCashIn = cashEntries.filter { it.type == "CASH_IN" }.sumOf { it.amount }
    val totalCashOut = cashEntries.filter { it.type == "CASH_OUT" }.sumOf { it.amount }
    val netCashInHand = totalCashIn - totalCashOut
    val netBusinessWorth = totalCustomerDue + netCashInHand - totalSupplierDue

    val creatorName = "এমডি রবিউল ইসলাম (Md. Rabiul Islam)"
    val creatorEmail = "rabiul.baishari@gmail.com"
    val creatorPhone = "+8801700000000"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business Overall Report Card
        Text("ব্যবসায়ের সার্বিক সারসংক্ষেপ", fontWeight = FontWeight.Bold, fontSize = 17.sp)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Customer Due
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("মোট কাস্টমার বাকি পাওনা", color = Color.Gray, fontSize = 13.5.sp)
                    Text("৳ %.2f".format(totalCustomerDue), color = TallyRedDue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Supplier Due
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("মোট মহাজন দেনা (তারা পাবে)", color = Color.Gray, fontSize = 13.5.sp)
                    Text("৳ %.2f".format(totalSupplierDue), color = TallyRedDue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Cash in hand
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ক্যাশবাক্স ব্যালেন্স (হাতে নগদ)", color = Color.Gray, fontSize = 13.5.sp)
                    Text("৳ %.2f".format(netCashInHand), color = TallyGreenIncome, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Divider()

                // Net Business Capital
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("নেট চলতি ব্যবসার মূলধন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("(পাওনা + ক্যাশ - দেনা)", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        "৳ %.2f".format(netBusinessWorth),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (netBusinessWorth >= 0) TallyPrimary else TallyRedDue
                    )
                }
            }
        }

        // Cloud Storage & Backup Section
        Text("ক্লাউড ব্যাকআপ ও ডাটা সংরক্ষণ", fontWeight = FontWeight.Bold, fontSize = 17.sp)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(TallyGreenIncomeLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = TallyGreenIncome)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ক্লাউড ব্যাকআপ সক্রিয়", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("সকল তথ্য ক্লাউডে সংরক্ষণ রয়েছে", fontSize = 12.sp, color = TallyGreenIncome)
                    }
                }

                val backupTime = profile?.lastBackupTimestamp ?: System.currentTimeMillis()
                Text("সর্বশেষ ব্যাকআপ: ${sdf.format(Date(backupTime))}", fontSize = 12.sp, color = Color.Gray)

                Button(
                    onClick = onTriggerCloudSync,
                    colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("sync_cloud_now_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("এখনই ক্লাউডে ব্যাকআপ নিন", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Security Settings
        Text("নিরাপত্তা ও বায়োমেট্রিক", fontWeight = FontWeight.Bold, fontSize = 17.sp)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Biometric Toggle (Face & Fingerprint)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TallyPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ফেস ও ফিঙ্গারপ্রিন্ট লগইন", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("বায়োমেট্রিক আনলক সক্রিয় করুন", fontSize = 11.5.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = profile?.isBiometricEnabled ?: true,
                        onCheckedChange = { onToggleBiometric(it) },
                        modifier = Modifier.testTag("biometric_setting_toggle")
                    )
                }

                Divider()

                // Lock App Button
                OutlinedButton(
                    onClick = onLockApp,
                    modifier = Modifier.fillMaxWidth().testTag("lock_app_now_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অ্যাপ অবিলম্বে লক করুন")
                }
            }
        }

        // Creator Profile & About Card
        Text("অ্যাপ পরিচিতি ও ক্রিয়েটর", fontWeight = FontWeight.Bold, fontSize = 17.sp)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = TallyPrimaryLight.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(TallyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(creatorName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TallyPrimary)
                        Text(creatorEmail, fontSize = 12.5.sp, color = Color.DarkGray)
                    }
                }

                Text(
                    "স্বাগতম টালিখাতা অ্যাপস! এই অ্যাপের এবাউটে ক্রিয়েটরের নাম, ইমেইল এবং ফোন নাম্বার সংযুক্ত আছে।",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$creatorPhone")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কল", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$creatorEmail")
                                putExtra(Intent.EXTRA_SUBJECT, "টালিখাতা অ্যাপ যোগাযোগ")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ইমেইল", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onShowAboutDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বিস্তারিত", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
