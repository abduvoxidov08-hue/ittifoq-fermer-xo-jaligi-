package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentType
import com.example.ui.theme.ClickBlue
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.PaymeTeal
import com.example.ui.theme.PaynetGreen
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun DashboardCard(
    farmerName: String,
    dehqonId: String,
    landSizeHa: Double,
    totalPaidUzs: Long,
    annualTargetUzs: Long,
    remainingDebtUzs: Long,
    completionPercentage: Double,
    activePaymentType: PaymentType,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (completionPercentage / 100.0).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Farm & Farmer info + Live Offline/Online badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ittifoq Fermer Xo'jaligi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenPrimary
                    )
                    Text(
                        text = "$farmerName • ID: $dehqonId",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Connection badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOnline) FarmGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStrokeOrNull(isOnline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.CloudOff,
                            contentDescription = if (isOnline) "Onlayn" else "Oflayn",
                            tint = if (isOnline) FarmGreenPrimary else Color(0xFFC62828),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOnline) "Onlayn" else "Oflayn rejim",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) FarmGreenPrimary else Color(0xFFC62828)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Gradient Card: Total Paid vs Annual Goal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FarmGreenPrimary, FarmGreenDark)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = HarvestGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Jami To'langan / Yillik Reja",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        // Percentage badge
                        Surface(
                            shape = CircleShape,
                            color = HarvestGold
                        ) {
                            Text(
                                text = "${String.format("%.1f", completionPercentage)}%",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E1C00)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${formatMoney(totalPaidUzs)} UZS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Yillik reja marrasi: ${formatMoney(annualTargetUzs)} UZS",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HarvestGold,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Summary Metric Cards: Remaining Debt & Official Payment Channel Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Remaining Debt Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("remaining_debt_metric"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (remainingDebtUzs <= 0) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Qolgan Qarzdorlik",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (remainingDebtUzs <= 0) StatusSuccess else StatusWarning
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (remainingDebtUzs <= 0) "Qarzdorlik Yo'q" else "${formatMoney(remainingDebtUzs)} UZS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingDebtUzs <= 0) StatusSuccess else Color(0xFFBF360C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (remainingDebtUzs <= 0) "100% Yopilgan" else "Reja doirasida",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Official Payment Status for Channels Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("payment_status_metric"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "To'lov Holati",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(activePaymentType.brandColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activePaymentType.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rasmiy tasdiqlangan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Land & Channel pills strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Yer maydoni: $landSizeHa gektar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Active channels badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChannelBadge("Click", ClickBlue)
                    ChannelBadge("Paynet", PaynetGreen)
                    ChannelBadge("Payme", PaymeTeal)
                    ChannelBadge("Naqd", Color(0xFFE65100))
                }
            }
        }
    }
}

@Composable
private fun ChannelBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun BorderStrokeOrNull(isOnline: Boolean): androidx.compose.foundation.BorderStroke? {
    return if (!isOnline) {
        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
    }
}

private fun formatMoney(amount: Long): String {
    return "%,d".format(amount).replace(',', ' ')
}
