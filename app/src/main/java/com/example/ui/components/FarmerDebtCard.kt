package com.example.ui.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FarmerDebtCard(
    farmerName: String,
    landSizeHa: Double,
    remainingDebtUzs: Long,
    paidAmountUzs: Long,
    annualPlanUzs: Long,
    paymentCount: Int,
    isRahbar: Boolean = false,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = onPayClick
) {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("uz-UZ"))
    val monthlyPayment = if (annualPlanUzs > 0) annualPlanUzs / 12L else 0L

    val completionPct = if (annualPlanUzs > 0) {
        ((paidAmountUzs.toDouble() / annualPlanUzs.toDouble()) * 100.0).coerceIn(0.0, 100.0)
    } else 0.0

    val hasDebt = remainingDebtUzs > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("farmer_debt_card_${farmerName.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Top Row: Avatar + Name + Land tag + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Profile Avatar (Special gold "R" medal badge for Rahbar as in @image2)
                    FarmerProfileAvatar(isRahbar = isRahbar)

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = farmerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Land size pill (e.g. 📍 2 gektar)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.testTag("land_pill_$farmerName")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${if (landSizeHa % 1.0 == 0.0) landSizeHa.toInt() else landSizeHa} gektar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                // Status Tag (Top Right)
                if (hasDebt) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Qarzdorlik bor",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD1FAE5)
                    ) {
                        Text(
                            text = "✅ Reja bajarildi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Debt Section: Caption & Large bold red amount
            Text(
                text = "Qolgan qarz miqdori",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${formatter.format(remainingDebtUzs)} so'm",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF991B1B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Capsule Progress Bar exactly like @image1
            CustomCapsuleProgressBar(
                progress = (completionPct / 100.0).toFloat(),
                paidText = "${formatter.format(paidAmountUzs)} so'm to'langan",
                percentText = "${completionPct.toInt()}%"
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle: Ijro: X% (N-ta to'lov) • Reja: X so'm
            Text(
                text = "Ijro: ${"%.1f".format(completionPct)}% ($paymentCount-ta to'lov) • Reja: ${formatter.format(annualPlanUzs)} so'm",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row: Monthly payment info (12 oy) + "+ To'lov" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Oylik to'lov (12 oy): ",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${formatter.format(monthlyPayment)} so'm/oy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onPayClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("pay_button_${farmerName.lowercase().replace(" ", "_")}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "To'lov",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Avatar with support for Dehqon (@image1) and Rahbar with gold medal badge (@image2)
 */
@Composable
fun FarmerProfileAvatar(
    isRahbar: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(54.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRahbar) {
            // Rahbar Profile Avatar with Gold Border and Medal Badge (@image2)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2E3B7B))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFF6E27A),
                                Color(0xFFCB9B51),
                                Color(0xFFFDF0A6),
                                Color(0xFF9E6B1F)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Rahbar",
                    tint = Color(0xFF8F9DE8),
                    modifier = Modifier.size(34.dp)
                )
            }

            // Overlapping Gold Medal with letter "R" at bottom
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .size(20.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF784F17),
                        shape = CircleShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFFF2A8),
                                    Color(0xFFD8A53B),
                                    Color(0xFFA67123)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "R",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = Color(0xFF4A2E05)
                    )
                }
            }
        } else {
            // Dehqon Profile Avatar (@image1)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF273766)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Dehqon",
                    tint = Color(0xFF7989C7),
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

/**
 * Capsule Progress Bar with filled emerald text and unfilled gray text as in @image1
 */
@Composable
private fun CustomCapsuleProgressBar(
    progress: Float,
    paidText: String,
    percentText: String,
    modifier: Modifier = Modifier
) {
    val clampedProgress = progress.coerceIn(0.05f, 1.0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF64748B)) // Gray background track
    ) {
        // Filled emerald section
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF10B981))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = paidText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }

        // Percentage text on the far right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(end = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = percentText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
