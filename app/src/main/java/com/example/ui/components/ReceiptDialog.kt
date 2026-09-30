package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionReceipt
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusSuccess

@Composable
fun ReceiptDialog(
    receipt: TransactionReceipt,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("official_receipt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close action & Status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RASMIY CHEK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Yopish"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Header & Republic emblem text
                Text(
                    text = "O'ZBEKISTON RESPUBLIKASI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "«ITTIFOQ FERMER XO'JALIGI»",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FarmGreenDark
                )
                Text(
                    text = "G'aznachilik va To'lovlar Qabul Markazi",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                DashedDivider()

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Number & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Chek Raqami:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = receipt.receiptNumber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = FarmGreenPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Sana va Vaqt:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = receipt.formattedDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Farmer info table card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF4F7F4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ReceiptRow(label = "Dehqon (Ism / ID):", value = "${receipt.farmerName} (${receipt.dehqonId})")
                        ReceiptRow(label = "Ekin Maydoni:", value = "${receipt.landSizeHectares} Gektar")
                        ReceiptRow(label = "To'lov Tizimi:", value = "${receipt.paymentType.title} (${receipt.paymentType.subtitle})")
                        if (receipt.expenseCategory.isNotBlank()) {
                            ReceiptRow(label = "Xarajat / Chiqim:", value = receipt.expenseCategory, isBold = true, valueColor = FarmGreenPrimary)
                        }
                        ReceiptRow(label = "Fiskal Reestr Kodi:", value = receipt.referenceCode, isMonospace = true)

                        // Telegram notification badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Telegram Xabarnoma:", fontSize = 12.sp, color = Color.DarkGray)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (receipt.telegramSent) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = if (receipt.telegramSent) "Botga Yuborilgan ✅" else "Oflayn Saqlangan 📥",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (receipt.telegramSent) FarmGreenPrimary else Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Financial Breakdown (Step 2 Logic)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TO'LANGAN SUMMA:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark
                            )
                            Text(
                                text = "${formatMoney(receipt.paymentAmountUzs)} UZS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarmGreenDark
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFC8E6C9)
                        )

                        ReceiptRow(
                            label = "Yillik Reja Maqsadi:",
                            value = "${formatMoney(receipt.annualPlanTargetUzs)} UZS"
                        )
                        ReceiptRow(
                            label = "Qolgan Qarzdorlik:",
                            value = "${formatMoney(receipt.remainingDebtUzs)} UZS",
                            valueColor = if (receipt.remainingDebtUzs <= 0) StatusSuccess else Color(0xFFC62828),
                            isBold = true
                        )
                        ReceiptRow(
                            label = "Reja Bajarilishi:",
                            value = "${String.format("%.1f", receipt.completionPercentage)}%",
                            isBold = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Official Verification Stamp / Seal Graphic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Signatures info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fermer xo'jaligi rahbari:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = receipt.farmManagerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Elektron raqamli tasdiq: IMZOLANDI",
                            fontSize = 10.sp,
                            color = FarmGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Circular Official Stamp Seal
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .border(2.dp, FarmGreenPrimary, CircleShape)
                            .background(FarmGreenPrimary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ITTIFOQ\nMUHRI",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarmGreenPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 10.sp
                            )
                            Text(
                                text = "TASDIQLANDI",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Share receipt & Copy text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val text = buildReceiptShareText(receipt)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Ittifoq Chek", text)
                            clipboard.setPrimaryClip(clip)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Nusxa olish", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val text = buildReceiptShareText(receipt)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Ittifoq Fermer Xo'jaligi Cheki - ${receipt.receiptNumber}")
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Chekni yuborish"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Ulashish", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    isMonospace: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
            strokeWidth = 2f
        )
    }
}

private fun buildReceiptShareText(receipt: TransactionReceipt): String {
    val expenseText = if (receipt.expenseCategory.isNotBlank()) "Chiqim yo'nalishi: ${receipt.expenseCategory}\n" else ""
    return """
        📄 RASMIY TO'LOV CHEKI
        «ITTIFOQ FERMER XO'JALIGI»
        ----------------------------------
        Chek №: ${receipt.receiptNumber}
        Sana: ${receipt.formattedDate}
        Dehqon: ${receipt.farmerName} (${receipt.dehqonId})
        Maydon: ${receipt.landSizeHectares} ga
        To'lov usuli: ${receipt.paymentType.title}
        ${expenseText}Reestr kodi: ${receipt.referenceCode}
        ----------------------------------
        To'langan summa: ${formatMoney(receipt.paymentAmountUzs)} UZS
        Yillik reja: ${formatMoney(receipt.annualPlanTargetUzs)} UZS
        Qolgan qarz: ${formatMoney(receipt.remainingDebtUzs)} UZS
        Bajarilishi: ${String.format("%.1f", receipt.completionPercentage)}%
        Holati: RASMIY TASDIQLANDI
        Telegram Bot: ${if (receipt.telegramSent) "Yuborilgan ✅" else "Oflayn"}
        ----------------------------------
        Fermer xo'jaligi rahbari:
        Abdumalikov Abdurahmon (Muhrlangan)
    """.trimIndent()
}

private fun formatMoney(amount: Long): String {
    return "%,d".format(amount).replace(',', ' ')
}
