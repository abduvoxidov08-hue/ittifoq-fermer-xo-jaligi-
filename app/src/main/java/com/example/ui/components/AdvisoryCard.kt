package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgriculturalAdvisory
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HarvestGoldDark

@Composable
fun AdvisoryCard(
    advisory: AgriculturalAdvisory?,
    isLoading: Boolean,
    isOnline: Boolean,
    landSizeHa: Double,
    onRefresh: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("advisory_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with AI/Offline badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (advisory?.isOfflineEngine == true) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (advisory?.isOfflineEngine == true) Icons.Default.Shield else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (advisory?.isOfflineEngine == true) FarmGreenPrimary else HarvestGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Agronom Maslahatchisi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (advisory?.isOfflineEngine == true) "Oflayn rejim (Dalada ishlash)" else "Gemini 3.5 Flash sun'iy intellekt",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (advisory?.isOfflineEngine == true) FarmGreenPrimary else HarvestGoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButtonOrLoading(
                    isLoading = isLoading,
                    onClick = onRefresh
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = FarmGreenPrimary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isOnline) "Gemini 3.5 Flash fermer uchun tavsiyalar tayyorlamoqda..." else "Oflayn agronom hisob-kitoblari yangilanmoqda...",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            } else if (advisory != null) {
                // Summary Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FarmGreenContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = advisory.seasonNotice,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = advisory.summary,
                            fontSize = 13.sp,
                            color = FarmGreenDark,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fertilizer & Water strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdvisoryMetricTile(
                        icon = Icons.Default.Science,
                        title = "Mineral O'g'it",
                        description = advisory.fertilizerSchedule,
                        iconTint = FarmGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdvisoryMetricTile(
                        icon = Icons.Default.Opacity,
                        title = "Sug'orish Rejasi",
                        description = advisory.waterManagement,
                        iconTint = Color(0xFF0277BD),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next Agricultural Actions List
                Text(
                    text = "Keyingi Agrotexnik Qadamlar (${landSizeHa} ga):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                advisory.nextActions.forEachIndexed { index, action ->
                    ActionStepRow(stepNumber = index + 1, text = action)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detailed Recommendations Accordion / List
                Text(
                    text = "Asosiy Tavsiyanomalar:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                advisory.recommendations.forEach { rec ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = FarmGreenPrimary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rec,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Offline Mode Test / Toggle Button
                OutlinedButton(
                    onClick = onToggleOfflineMode,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_offline_button")
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.CloudOff else Icons.Default.Eco,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = FarmGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnline) "Dala (Oflayn) Rejimini Sinash" else "Tarmoqqa Ulanish (Onlayn)",
                        fontSize = 12.sp,
                        color = FarmGreenPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvisoryMetricTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun ActionStepRow(stepNumber: Int, text: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = FarmGreenPrimary,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$stepNumber",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun IconButtonOrLoading(isLoading: Boolean, onClick: () -> Unit) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = FarmGreenPrimary
        )
    } else {
        androidx.compose.material3.IconButton(
            onClick = onClick,
            modifier = Modifier.testTag("refresh_advisory_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Yangilash",
                tint = FarmGreenPrimary
            )
        }
    }
}
