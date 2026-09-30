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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentType
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentFormCard(
    farmerName: String,
    dehqonId: String,
    landSizeInput: String,
    paymentAmountInput: String,
    annualPlanInput: String,
    selectedPaymentType: PaymentType,
    notesInput: String,
    selectedExpenseCategory: String,
    isRahbar: Boolean,
    calculatedRemainingBalance: Long,
    calculatedCompletionPercentage: Double,
    isProcessing: Boolean,
    onFarmerNameOrIdChanged: (String) -> Unit,
    onLandSizeChanged: (String) -> Unit,
    onPaymentAmountChanged: (String) -> Unit,
    onAnnualPlanTargetChanged: (String) -> Unit,
    onPaymentTypeChanged: (PaymentType) -> Unit,
    onExpenseCategoryChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onPresetAmountSelected: (Long) -> Unit,
    onOpenFarmerSelector: () -> Unit,
    onSubmitPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expenseCategories = listOf(
        "Yoqilg'i / Solyarka",
        "Mineral O'g'it (Ammofos/Selitra)",
        "Traktor xizmati / Agrotexnika",
        "Urug'lik xaridi",
        "Sug'orish / Nasos xizmati",
        "Ish haqi / Avans",
        "Boshqa xarajat"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("payment_form_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Form Title & Role badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = FarmGreenPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isRahbar) "👑" else "🌾",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isRahbar) "To'lov Qabul Qilish (Rahbar)" else "Karta Orqali To'lov Yuborish",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRahbar) "Karta va Naqd pul qabuli hamda chiqim hisobi" else "Click / Payme / Paynet orqali to'lov",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isRahbar) {
                    IconButton(
                        onClick = onOpenFarmerSelector,
                        modifier = Modifier.testTag("switch_farmer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Dehqonni almashtirish",
                            tint = FarmGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dehqon Name display or selector
            OutlinedTextField(
                value = farmerName,
                onValueChange = onFarmerNameOrIdChanged,
                enabled = isRahbar,
                label = { Text(if (isRahbar) "Dehqon (Ism yoki ID)" else "Dehqon Kabineti") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = FarmGreenPrimary
                    )
                },
                trailingIcon = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .then(if (isRahbar) Modifier.clickable { onOpenFarmerSelector() } else Modifier)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = dehqonId,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenPrimary
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_farmer_name"),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Land size in hectares & Annual Plan Target Amount (UZS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = landSizeInput,
                    onValueChange = onLandSizeChanged,
                    enabled = isRahbar,
                    label = { Text("Maydon (ga)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            tint = FarmGreenPrimary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_land_size"),
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedFieldColors()
                )

                OutlinedTextField(
                    value = annualPlanInput,
                    onValueChange = onAnnualPlanTargetChanged,
                    enabled = isRahbar,
                    label = { Text("Yillik Reja (UZS)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = FarmGreenPrimary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("input_annual_plan"),
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Types:
            // Dehqon: ONLY Click, Payme, Paynet (NO Cash / Naqd!)
            // Rahbar: BOTH Naqd pul AND Karta (Click, Paynet, Payme)
            Text(
                text = if (isRahbar) "To'lov Turi (Karta va Naqd pul):" else "To'lov Usuli (Karta orqali):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isRahbar) {
                    PaymentTypeOption(
                        type = PaymentType.CASH,
                        icon = Icons.Default.Money,
                        isSelected = selectedPaymentType == PaymentType.CASH,
                        modifier = Modifier.weight(1f),
                        onSelect = { onPaymentTypeChanged(PaymentType.CASH) }
                    )
                }

                PaymentTypeOption(
                    type = PaymentType.CLICK,
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedPaymentType == PaymentType.CLICK,
                    modifier = Modifier.weight(1f),
                    onSelect = { onPaymentTypeChanged(PaymentType.CLICK) }
                )
                PaymentTypeOption(
                    type = PaymentType.PAYME,
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedPaymentType == PaymentType.PAYME,
                    modifier = Modifier.weight(1f),
                    onSelect = { onPaymentTypeChanged(PaymentType.PAYME) }
                )
                PaymentTypeOption(
                    type = PaymentType.PAYNET,
                    icon = Icons.Default.PointOfSale,
                    isSelected = selectedPaymentType == PaymentType.PAYNET,
                    modifier = Modifier.weight(1f),
                    onSelect = { onPaymentTypeChanged(PaymentType.PAYNET) }
                )
            }

            if (!isRahbar) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔒 Dehqonlar faqat karta orqali to'lov qiladi. Naqd to'lov qabul qilinmaydi.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RAHBAR ONLY: Chiqim / Xarajat Kategoriyasi
            // "rahbar karta orqali tolov pasida qanaqa chimga ketgani yozishi kere boladi yoki kateoriyadan tanlashi kere boladi"
            if (isRahbar) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Chiqim / Xarajat Yo'nalishi:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            expenseCategories.forEach { cat ->
                                val isCatSelected = selectedExpenseCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCatSelected) FarmGreenPrimary else MaterialTheme.colorScheme.surface,
                                    border = if (isCatSelected) null else androidx.compose.foundation.BorderStroke(0.8.dp, Color.LightGray),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onExpenseCategoryChanged(cat) }
                                ) {
                                    Text(
                                        text = cat,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        fontSize = 11.sp,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Payment Amount (in UZS)
            OutlinedTextField(
                value = paymentAmountInput,
                onValueChange = onPaymentAmountChanged,
                label = { Text("To'langan Summa (UZS)") },
                placeholder = { Text("10 000 000") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Money,
                        contentDescription = null,
                        tint = FarmGreenPrimary
                    )
                },
                supportingText = {
                    val amountLong = paymentAmountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
                    if (amountLong > 0) {
                        Text(
                            text = "${formatMoney(amountLong)} UZS",
                            color = FarmGreenPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_payment_amount"),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Presets Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip("+5 mln", 5_000_000L, onPresetAmountSelected)
                PresetChip("+10 mln", 10_000_000L, onPresetAmountSelected)
                PresetChip("+15 mln", 15_000_000L, onPresetAmountSelected)
                PresetChip("+20 mln", 20_000_000L, onPresetAmountSelected)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes / Transaction Check ID input
            OutlinedTextField(
                value = notesInput,
                onValueChange = onNotesChanged,
                label = { Text(if (isRahbar) "Qo'shimcha izoh / Chek raqami" else "Tranzaksiya kodi yoki izoh") },
                placeholder = { Text("Masalan: Click tranzaksiya #849102") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = FarmGreenPrimary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Calculation Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculation_logic_box")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Avtomatik Hisob-Kitob",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenPrimary
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = if (calculatedRemainingBalance <= 0) Color(0xFFC8E6C9) else Color(0xFFFFECB3)
                        ) {
                            Text(
                                text = if (calculatedRemainingBalance <= 0) "100% Yopildi" else "Reja doirasida",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (calculatedRemainingBalance <= 0) FarmGreenPrimary else Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Reja Bajarilishi", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "${String.format("%.1f", calculatedCompletionPercentage)}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Qolgan Qarz", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "${formatMoney(calculatedRemainingBalance)} UZS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (calculatedRemainingBalance <= 0) StatusSuccess else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Button: Submit & Send to Telegram Bot automatically
            Button(
                onClick = onSubmitPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_payment_button"),
                enabled = !isProcessing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarmGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Chek botga yuborilmoqda...")
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRahbar) "To'lovni Tasdiqlash va Botga Yuborish" else "To'lovni Yuborish va Botga Xabar Berish",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentTypeOption(
    type: PaymentType,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("payment_type_${type.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) type.containerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, type.brandColor) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = type.title,
                tint = if (isSelected) type.brandColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) type.brandColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    amount: Long,
    onSelect: (Long) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable { onSelect(amount) }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FarmGreenPrimary,
    focusedLabelColor = FarmGreenPrimary,
    cursorColor = FarmGreenPrimary
)

private fun formatMoney(amount: Long): String {
    return "%,d".format(amount).replace(',', ' ')
}
