package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentType
import com.example.data.model.TransactionReceipt
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.StatusSuccess

@Composable
fun TransactionHistorySection(
    payments: List<TransactionReceipt>,
    selectedFilter: PaymentType?,
    onFilterChange: (PaymentType?) -> Unit,
    onReceiptClick: (TransactionReceipt) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredPayments = if (selectedFilter == null) {
        payments
    } else {
        payments.filter { it.paymentType == selectedFilter }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_history_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "To'lovlar va Cheklar Reestri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = FarmGreenPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${filteredPayments.size} ta chek",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onFilterChange(null) },
                        label = { Text("Barchasi") },
                        colors = filterChipColors()
                    )
                }
                items(PaymentType.entries) { type ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { onFilterChange(type) },
                        label = { Text(type.title) },
                        colors = filterChipColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredPayments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ushbu toifa bo'yicha to'lovlar topilmadi",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredPayments.forEach { receipt ->
                        PaymentHistoryItem(
                            receipt = receipt,
                            onClick = { onReceiptClick(receipt) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentHistoryItem(
    receipt: TransactionReceipt,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("receipt_item_${receipt.receiptNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment Type Indicator
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = receipt.paymentType.containerColor,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = receipt.paymentType.title.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = receipt.paymentType.brandColor,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = receipt.receiptNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = FarmGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (receipt.isOfflineRecorded) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                text = "Oflayn",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${receipt.farmerName} • ${receipt.formattedDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${formatMoney(receipt.paymentAmountUzs)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FarmGreenDark
                )
                Text(
                    text = "Qarz: ${formatMoney(receipt.remainingDebtUzs)}",
                    fontSize = 10.sp,
                    color = if (receipt.remainingDebtUzs <= 0) StatusSuccess else Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = FarmGreenPrimary,
    selectedLabelColor = Color.White
)

private fun formatMoney(amount: Long): String {
    return "%,d".format(amount).replace(',', ' ')
}
