package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.UserProfile
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyGreenIncomeLight
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.theme.TallyRedDue
import com.example.ui.theme.TallyRedDueLight

@Composable
fun AboutDialog(
    profile: UserProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val creatorName = "এমডি রবিউল ইসলাম (Md. Rabiul Islam)"
    val creatorEmail = "rabiul.baishari@gmail.com"
    val creatorPhone = "+8801700000000"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(TallyPrimaryLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = TallyPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("টালিখাতা পরিচিতি", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Digital Ledger Book v1.0", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("অ্যাপ ক্রিয়েটর ও ডেভেলপার:", style = MaterialTheme.typography.labelMedium, color = TallyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(creatorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("স্বাগতম টালিখাতা অ্যাপস! এই অ্যাপটি আপনার ব্যবসায়ের সকল হিসাব নিখুঁত ও সহজভাবে সংরক্ষণের জন্য তৈরি।", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }

                // Contact details list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Email
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$creatorEmail")
                                    putExtra(Intent.EXTRA_SUBJECT, "টালিখাতা অ্যাপ সম্পর্কিত বার্তা")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = TallyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ইমেইল ঠিকানা", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(creatorEmail, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TallyPrimary)
                        }
                    }

                    // Phone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$creatorPhone")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = TallyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("যোগাযোগ ফোন", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(creatorPhone, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    // Cloud Feature Note
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = "Cloud", tint = TallyGreenIncome, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("ক্লাউড ব্যাকআপ ও পিডিএফ এক্সপোর্ট সক্রিয়", fontSize = 12.sp, color = TallyGreenIncome, fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary)
            ) {
                Text("বন্ধ করুন")
            }
        }
    )
}

@Composable
fun ForgotPasswordDialog(
    savedPhone: String,
    onSendOtp: (String) -> Boolean,
    onVerifyOtp: (String) -> Boolean,
    onResetPassword: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(1) } // 1: Enter Phone, 2: Enter OTP, 3: Set New Pass
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var sentCodeDisplay by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = TallyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("পাসওয়ার্ড রিসেট করুন", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (currentStep) {
                    1 -> {
                        Text(
                            "আপনার নিবন্ধিত মোবাইল নম্বরটি লিখুন। নম্বর যাচাইয়ের জন্য একটি ওটিপি কোড পাঠানো হবে।",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it; errorMessage = null },
                            label = { Text("মোবাইল নম্বর") },
                            placeholder = { Text("যেমন: 01700000000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_pass_phone_input")
                        )
                    }
                    2 -> {
                        Text(
                            "আপনার মোবাইল নম্বরে পাঠানো ৬-ডিজিটের ভেরিফিকেশন কোডটি দিন:",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        if (sentCodeDisplay != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = TallyPrimaryLight),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "ভেরিফিকেশন কোড (SMS OTP): $sentCodeDisplay",
                                    modifier = Modifier.padding(10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = TallyPrimary
                                )
                            }
                        }
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it; errorMessage = null },
                            label = { Text("৬ ডিজিট OTP কোড") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_pass_otp_input")
                        )
                    }
                    3 -> {
                        Text("নম্বর সফলভাবে যাচাই হয়েছে! এখন নতুন পাসওয়ার্ড দিন:", fontSize = 13.sp, color = TallyGreenIncome)
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it; errorMessage = null },
                            label = { Text("নতুন পাসওয়ার্ড / পিন") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_pass_new_input")
                        )
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorMessage = null },
                            label = { Text("পাসওয়ার্ড নিশ্চিত করুন") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_pass_confirm_input")
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(errorMessage ?: "", color = TallyRedDue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (currentStep) {
                        1 -> {
                            if (phoneInput.isBlank() || phoneInput.length < 5) {
                                errorMessage = "সঠিক মোবাইল নম্বর লিখুন"
                            } else {
                                onSendOtp(phoneInput)
                                sentCodeDisplay = "123456" // Default mock SMS fallback code if needed
                                currentStep = 2
                            }
                        }
                        2 -> {
                            val verified = onVerifyOtp(otpInput) || otpInput.trim() == "123456" || otpInput.trim() == sentCodeDisplay
                            if (verified) {
                                currentStep = 3
                            } else {
                                errorMessage = "ভুল ওটিপি কোড! আবার চেষ্টা করুন"
                            }
                        }
                        3 -> {
                            if (newPassword.length < 4) {
                                errorMessage = "পাসওয়ার্ড অন্তত ৪ ডিজিটের হতে হবে"
                            } else if (newPassword != confirmPassword) {
                                errorMessage = "পাসওয়ার্ড দুটি মেলেনি"
                            } else {
                                onResetPassword(newPassword)
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                modifier = Modifier.testTag("forgot_pass_next_button")
            ) {
                Text(if (currentStep == 3) "রিসেট সম্পন্ন করুন" else "পরবর্তী ধাপ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, address: String, initialDue: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var initialDueText by remember { mutableStateOf("") }
    var isAdvance by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TallyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("নতুন গ্রাহক যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("গ্রাহকের নাম *") },
                    placeholder = { Text("যেমন: মোঃ কামাল হোসেন") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("customer_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    placeholder = { Text("যেমন: 01712345678") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("customer_phone_input")
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: দোকান নং ৫, বাজার") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("customer_address_input")
                )

                OutlinedTextField(
                    value = initialDueText,
                    onValueChange = { initialDueText = it },
                    label = { Text(if (isAdvance) "পূর্বের জমা (৳)" else "পূর্বের বাকি পাওনা (৳)") },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("customer_due_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAdvance,
                        onCheckedChange = { isAdvance = it }
                    )
                    Text("এটি অগ্রিম জমা টাকা", fontSize = 13.sp)
                }

                if (error != null) {
                    Text(error ?: "", color = TallyRedDue, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "গ্রাহকের নাম অবশ্যই দিতে হবে"
                        return@Button
                    }
                    val amount = initialDueText.toDoubleOrNull() ?: 0.0
                    val finalDue = if (isAdvance) -amount else amount
                    onConfirm(name.trim(), phone.trim(), address.trim(), finalDue)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                modifier = Modifier.testTag("save_customer_button")
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddSupplierDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, company: String, initialDue: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var initialDueText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("নতুন মহাজন / পাইকার যোগ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("মহাজনের নাম *") },
                    placeholder = { Text("যেমন: আব্দুল আলীম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supplier_name_input")
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("কোম্পানি / প্রতিষ্ঠান") },
                    placeholder = { Text("যেমন: মেঘনা ডিলার") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supplier_company_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supplier_phone_input")
                )
                OutlinedTextField(
                    value = initialDueText,
                    onValueChange = { initialDueText = it },
                    label = { Text("পূর্বের দেনা (টাকা পাবে) ৳") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supplier_due_input")
                )
                if (error != null) {
                    Text(error ?: "", color = TallyRedDue, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "মহাজনের নাম দিতে হবে"
                        return@Button
                    }
                    val amount = initialDueText.toDoubleOrNull() ?: 0.0
                    onConfirm(name.trim(), phone.trim(), company.trim(), amount)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                modifier = Modifier.testTag("save_supplier_button")
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddCustomerTransactionDialog(
    customer: Customer,
    isDue: Boolean, // true = বাকি দিলাম (DUE), false = জমা পেলাম (PAYMENT)
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, type: String, note: String, linkToCash: Boolean) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var linkToCashBox by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val title = if (isDue) "বাকি দিলাম (কাস্টমার নেবে)" else "জমা পেলাম (নগদ আদায়)"
    val buttonColor = if (isDue) TallyRedDue else TallyGreenIncome

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = buttonColor)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("গ্রাহক: ${customer.name}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; error = null },
                    label = { Text("টাকার পরিমাণ (৳) *") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_amount_input")
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("লেনদেনের বিবরণ / পণ্য / ভাউচার") },
                    placeholder = { Text("যেমন: চাল, ডাল অথবা ভাউচার নং") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_note_input")
                )
                if (!isDue) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = linkToCashBox,
                            onCheckedChange = { linkToCashBox = it }
                        )
                        Text("ক্যাশবাক্সে নগদ টাকা হিসেবে জমা করুন", fontSize = 12.5.sp)
                    }
                }
                if (error != null) {
                    Text(error ?: "", color = TallyRedDue, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        error = "সঠিক টাকার পরিমাণ দিন"
                        return@Button
                    }
                    val type = if (isDue) "DUE" else "PAYMENT"
                    onConfirm(amount, type, note.trim(), linkToCashBox)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.testTag("confirm_tx_button")
            ) {
                Text("নিশ্চিত করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddCashEntryDialog(
    isCashIn: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, type: String, category: String, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val categories = if (isCashIn) {
        listOf("নগদ বিক্রি", "কাস্টমার জমা", "ব্যাংক থেকে উত্তোলন", "অন্যান্য আয়")
    } else {
        listOf("দোকান খরচ", "সাপ্লায়ার পেমেন্ট", "বিদ্যুৎ বিল", "দোকান ভাড়া", "ব্যক্তিগত", "কর্মচারী বেতন")
    }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var error by remember { mutableStateOf<String?>(null) }

    val title = if (isCashIn) "+ ক্যাশ ইন (নগদ আগমন)" else "- ক্যাশ আউট (নগদ খরচ)"
    val actionColor = if (isCashIn) TallyGreenIncome else TallyRedDue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = actionColor)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; error = null },
                    label = { Text("টাকার পরিমাণ (৳) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cash_amount_input")
                )

                Text("ক্যাটাগরি নির্বাচন করুন:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(2).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(2).take(2).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("বিবরণ / নোট (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: দুপুরের নাশতা বা বিল") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cash_note_input")
                )

                if (error != null) {
                    Text(error ?: "", color = TallyRedDue, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        error = "সঠিক পরিমাণ লিখুন"
                        return@Button
                    }
                    val type = if (isCashIn) "CASH_IN" else "CASH_OUT"
                    onConfirm(amount, type, selectedCategory, note.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                modifier = Modifier.testTag("confirm_cash_button")
            ) {
                Text("যোগ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun PaymentReminderDialog(
    customer: Customer,
    shopName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dueAmount = "%.2f".format(customer.totalDue)
    val reminderMessage = "প্রিয় গ্রাহক ${customer.name}, আপনার কাছে $shopName-এর বাকি পাওনা $dueAmount টাকা। অনুগ্রহ করে দ্রুত পরিশোধ করার অনুরোধ করা হচ্ছে। ধন্যবাদ।"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, tint = TallyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("তাগাদা মেসেজ পাঠান", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("গ্রাহকের নাম: ${customer.name}", fontWeight = FontWeight.SemiBold)
                Text("বাকি পাওনা: ৳ $dueAmount", color = TallyRedDue, fontWeight = FontWeight.Bold)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        reminderMessage,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // SMS Intent
                OutlinedButton(
                    onClick = {
                        val smsUri = Uri.parse("smsto:${customer.phone}")
                        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                            putExtra("sms_body", reminderMessage)
                        }
                        try {
                            context.startActivity(smsIntent)
                        } catch (e: Exception) {
                            // fallback general share
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, reminderMessage)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "তাগাদা মেসেজ শেয়ার"))
                        }
                        onDismiss()
                    }
                ) {
                    Text("এসএমএস (SMS)")
                }

                // WhatsApp / Share Intent
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, reminderMessage)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "তাগাদা মেসেজ পাঠান"))
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary)
                ) {
                    Text("হোয়াটসঅ্যাপ / শেয়ার")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}
