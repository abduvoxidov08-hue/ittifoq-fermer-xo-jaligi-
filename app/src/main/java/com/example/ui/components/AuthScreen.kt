package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold

data class AccountPreset(
    val username: String,
    val pass: String,
    val label: String,
    val role: String,
    val iconEmoji: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    loginUsernameInput: String,
    loginPasswordInput: String,
    loginError: String?,
    isAuthenticating: Boolean,
    onLoginUsernameChanged: (String) -> Unit,
    onLoginPasswordChanged: (String) -> Unit,
    onQuickSelectAccount: (String, String) -> Unit,
    onLoginSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Predefined accounts according to farm management specifications
    val leaderAccount = AccountPreset("abdurahmon", "rahbar", "Abdurahmon (Rahbar)", "RAHBAR", "👑")
    val dehqonAccounts = listOf(
        AccountPreset("abduvosiq", "kabinet1", "Abduvosiq", "DEHQON", "🌾"),
        AccountPreset("saidkarim", "kabinet2", "Saidkarim", "DEHQON", "🌾"),
        AccountPreset("sobit", "kabinet3", "Sobit", "DEHQON", "🌾"),
        AccountPreset("karim", "kabinet4", "Karim", "DEHQON", "🌾"),
        AccountPreset("mirzohid", "kabinet5", "Mirzohid", "DEHQON", "🌾"),
        AccountPreset("ravshan", "kabinet6", "Ravshan", "DEHQON", "🌾"),
        AccountPreset("abdujabbor", "kabinet7", "Abdujabbor", "DEHQON", "🌾"),
        AccountPreset("xolmurod", "kabinet8", "Xolmurod", "DEHQON", "🌾"),
        AccountPreset("hamidulla", "kabinet9", "Hamidulla", "DEHQON", "🌾"),
        AccountPreset("ubaydulla", "kabinet10", "Ubaydulla", "DEHQON", "🌾"),
        AccountPreset("shuhrat", "kabinet11", "Shuhrat", "DEHQON", "🌾")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Emblem & App Header
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(FarmGreenPrimary, FarmGreenDark))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = HarvestGold,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "«Ittifoq Fermer Xo'jaligi»",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = FarmGreenDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Rahbar: Abdumalikov Abdurahmon • Tizimga Kirish",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message if any
        AnimatedVisibility(visible = loginError != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFEBEE),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = loginError ?: "",
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFFC62828),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Clean Login Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Shaxsiy Kabinetga Kirish",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenDark
                )
                Text(
                    text = "Hisobingizga kirish uchun login va parolni kiriting",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = loginUsernameInput,
                    onValueChange = onLoginUsernameChanged,
                    label = { Text("Login") },
                    placeholder = { Text("Masalan: abdurahmon yoki abduvosiq") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_login_username"),
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = loginPasswordInput,
                    onValueChange = onLoginPasswordChanged,
                    label = { Text("Parol") },
                    placeholder = { Text("Parolingizni kiriting") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FarmGreenPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Parolni yashirish" else "Parolni ko'rsatish"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_login_password"),
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedFieldColors()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onLoginSubmit,
                    enabled = !isAuthenticating,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("button_login_submit")
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tekshirilmoqda...")
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Kabinetga Kirish", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Quick Login Helper Section for All Predefined Accounts
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "🔑 Tezkor Kirish (Bir teginishda):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FarmGreenDark
                )
                Text(
                    text = "Quyidagi hisoblardan birini tanlab tezda kabinetga kiring:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Leader Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FarmGreenContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onQuickSelectAccount(leaderAccount.username, leaderAccount.pass)
                        }
                        .testTag("quick_login_rahbar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = leaderAccount.iconEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = leaderAccount.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FarmGreenDark
                                )
                                Text(
                                    text = "login: ${leaderAccount.username} • parol: ${leaderAccount.pass}",
                                    fontSize = 11.sp,
                                    color = FarmGreenDark.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Text(
                            text = "Kirish ➔",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = FarmGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Dehqonlar kabinetlari:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dehqon Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dehqonAccounts.forEach { acc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    FarmGreenPrimary.copy(alpha = 0.25f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onQuickSelectAccount(acc.username, acc.pass)
                                }
                                .testTag("quick_login_${acc.username}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = acc.iconEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = acc.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FarmGreenDark
                                    )
                                    Text(
                                        text = acc.pass,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FarmGreenPrimary,
    focusedLabelColor = FarmGreenPrimary,
    cursorColor = FarmGreenPrimary
)
