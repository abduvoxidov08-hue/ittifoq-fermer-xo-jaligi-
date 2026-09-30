package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClickBlue
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.PaymeTeal

@Composable
fun FarmBankCard(
    onCopyToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Foydalanuvchi taqdim etgan rasmiy karta raqami: 5614682706098243
    val rawCardNumber = "5614682706098243"
    val formattedCardNumber = "5614 6827 0609 8243"
    val cardHolder = "ABDUMALIKOV ABDURAHMON"
    val bankName = "AGROBANK ATB • «ITTIFOQ» FX"

    var isCopied by remember { mutableStateOf(false) }

    fun copyToClipboard(source: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Ittifoq Farm Card", rawCardNumber)
        clipboard.setPrimaryClip(clip)
        isCopied = true
        onCopyToast("Karta raqami ($formattedCardNumber) nusxalandi!")
    }

    fun openPaymentApp(packageName: String, deepLink: String, webUrl: String, appName: String) {
        copyToClipboard(appName)
        try {
            val deepIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (context.packageManager.resolveActivity(deepIntent, 0) != null) {
                context.startActivity(deepIntent)
                return
            }
        } catch (_: Exception) {}

        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                return
            }
        } catch (_: Exception) {}

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "$appName ilovasini ochib bo'lmadi", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("farm_bank_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0D3B16),
                            Color(0xFF1B5E20),
                            Color(0xFF002900)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Card Top Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bankName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestGold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Card chip graphic
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.size(width = 34.dp, height = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .border(0.5.dp, Color(0xFF8B6B15), RoundedCornerShape(3.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Card Number display
                Text(
                    text = "Rasmiy to'lov kartasi (Click / Payme / Paynet):",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { copyToClipboard("Karta") }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formattedCardNumber,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Nusxa olish",
                        tint = if (isCopied) Color(0xFF81C784) else HarvestGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Holder & Expire
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Karta egasi (Fermer rahbari):",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Text(
                            text = cardHolder,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "HUMO / UZCARD",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1-Click Copy card button
                Button(
                    onClick = { copyToClipboard("Tugma") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("copy_card_number_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopied) Color(0xFF2E7D32) else HarvestGold,
                        contentColor = if (isCopied) Color.White else Color(0xFF2E1C00)
                    )
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCopied) "Karta raqami nusxalandi!" else "Karta raqamini nusxalash ($rawCardNumber)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // User requested: Click, Payme, and Paynet buttons
                // "click , paynet , payme baton usitaga bosa shu karta bilan tolov qilish uchun usha ilovada ochsin"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // CLICK BUTTON
                    Button(
                        onClick = {
                            openPaymentApp(
                                packageName = "uz.click.uz",
                                deepLink = "clickuz://payment?receiver=$rawCardNumber",
                                webUrl = "https://my.click.uz/pay",
                                appName = "Click"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("open_click_app_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClickBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(text = "Click", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                    }

                    // PAYME BUTTON
                    Button(
                        onClick = {
                            openPaymentApp(
                                packageName = "uz.payme",
                                deepLink = "payme://transfer?receiver=$rawCardNumber",
                                webUrl = "https://payme.uz/fallback",
                                appName = "Payme"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("open_payme_app_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaymeTeal,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(text = "Payme", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                    }

                    // PAYNET BUTTON
                    Button(
                        onClick = {
                            openPaymentApp(
                                packageName = "uz.paynet.app",
                                deepLink = "paynet://payment?card=$rawCardNumber",
                                webUrl = "https://paynet.uz",
                                appName = "Paynet"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("open_paynet_app_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100),
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(text = "Paynet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
