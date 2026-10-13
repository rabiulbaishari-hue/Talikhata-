package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.TallyGold
import com.example.ui.theme.TallyGreenIncome
import com.example.ui.theme.TallyPrimary
import com.example.ui.theme.TallyPrimaryDark
import com.example.ui.theme.TallyPrimaryLight
import com.example.ui.theme.TallyRedDue

@Composable
fun FirstTimeSetupScreen(
    onCompleteSetup: (
        shopName: String,
        ownerName: String,
        phone: String,
        email: String,
        password: String,
        enableBiometric: Boolean
    ) -> Unit,
    onShowAbout: () -> Unit
) {
    var shopName by remember { mutableStateOf("রবিউল ট্রেডার্স") }
    var ownerName by remember { mutableStateOf("এমডি রবিউল ইসলাম") }
    var phone by remember { mutableStateOf("01700000000") }
    var email by remember { mutableStateOf("rabiul.baishari@gmail.com") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var enableBiometric by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TallyPrimary, TallyPrimaryDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // App Icon & Greeting
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Tally Book",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "স্বাগতম টালিখাতা অ্যাপস",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "ক্রিয়েট বাই এমডি রবিউল ইসলাম\n(rabiul.baishari@gmail.com)",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Setup Form Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "প্রথমবার ব্যবহারের অ্যাকাউন্ট সেটআপ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TallyPrimary
                    )

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("দোকান বা ব্যবসায়ের নাম *") },
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = TallyPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_shop_name_input")
                    )

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("মালিকের নাম *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_owner_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("মোবাইল নম্বর * (পাসওয়ার্ড রিকভারির জন্য)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TallyPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_phone_input")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ইমেইল ঠিকানা") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_email_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("নতুন পাসওয়ার্ড / পিন *") },
                        placeholder = { Text("৪ বা ৬ ডিজিটের পাসওয়ার্ড") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TallyPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_password_input")
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        label = { Text("পাসওয়ার্ড নিশ্চিত করুন *") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("setup_confirm_password_input")
                    )

                    // Biometric Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TallyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("ফেস ও ফিঙ্গারপ্রিন্ট লগইন", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                                Text("বায়োমেট্রিক দ্বারা দ্রুত আনলক করুন", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = enableBiometric,
                            onCheckedChange = { enableBiometric = it }
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            errorMessage ?: "",
                            color = TallyRedDue,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            if (shopName.isBlank()) {
                                errorMessage = "দোকানের নাম লিখুন"
                            } else if (password.length < 4) {
                                errorMessage = "পাসওয়ার্ড অন্তত ৪ ডিজিটের হতে হবে"
                            } else if (password != confirmPassword) {
                                errorMessage = "উভয় পাসওয়ার্ড একই হতে হবে"
                            } else {
                                onCompleteSetup(
                                    shopName,
                                    ownerName,
                                    phone,
                                    email,
                                    password,
                                    enableBiometric
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("setup_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("অ্যাকাউন্ট তৈরি ও শুরু করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onShowAbout) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ক্রিয়েটর পরিচিতি ও এবাউট", color = Color.White.copy(alpha = 0.9f))
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun LockScreen(
    profile: UserProfile?,
    onUnlockWithPassword: (String) -> Boolean,
    onTriggerBiometric: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onShowAbout: () -> Unit
) {
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val shopTitle = profile?.shopName ?: "রবিউল ট্রেডার্স"
    val creatorName = "এমডি রবিউল ইসলাম"
    val creatorEmail = "rabiul.baishari@gmail.com"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TallyPrimary, TallyPrimaryDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "স্বাগতম টালিখাতা অ্যাপস",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                shopTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TallyGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "ক্রিয়েট বাই $creatorName\n($creatorEmail)",
                fontSize = 12.5.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Unlock Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "সুরক্ষিত অ্যাকাউন্ট আনলক",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TallyPrimary
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; errorMessage = null },
                        label = { Text("পাসওয়ার্ড / পিন কোড") },
                        placeholder = { Text("পাসওয়ার্ড লিখুন") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TallyPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("lock_password_input")
                    )

                    if (errorMessage != null) {
                        Text(
                            errorMessage ?: "",
                            color = TallyRedDue,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Unlock Button
                    Button(
                        onClick = {
                            if (passwordInput.isBlank()) {
                                errorMessage = "পাসওয়ার্ড লিখুন"
                            } else {
                                val success = onUnlockWithPassword(passwordInput)
                                if (!success) {
                                    errorMessage = "ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("lock_unlock_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TallyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("লগইন করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    // Biometric Unlock Button (Face & Fingerprint)
                    if (profile?.isBiometricEnabled != false) {
                        OutlinedButton(
                            onClick = onTriggerBiometric,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("biometric_login_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TallyPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = "Biometric",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ফেস ও ফিঙ্গারপ্রিন্ট দ্বারা লগইন", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    // Forgot Password Link
                    TextButton(
                        onClick = onForgotPasswordClick,
                        modifier = Modifier.testTag("forgot_password_button")
                    ) {
                        Text("পাসওয়ার্ড ভুলে গেছেন? (মোবাইল ভেরিফিকেশন)", color = TallyRedDue, fontSize = 12.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // About Button
            TextButton(onClick = onShowAbout) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.width(6.dp))
                Text("এবাউট ও ডেভেলপার তথ্য", color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}
