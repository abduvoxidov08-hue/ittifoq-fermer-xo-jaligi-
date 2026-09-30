package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.FarmerEntity
import com.example.data.local.PredefinedAccounts
import com.example.data.model.PaymentType
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.StatusSuccess
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentModalDialog(
    farmer: FarmerEntity,
    currentDebtUzs: Long,
    isRahbar: Boolean,
    onDismiss: () -> Unit,
    onSubmitPayment: (
        paymentType: PaymentType,
        amount: Long,
        expenseCategory: String,
        notes: String,
        receiptUri: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val monthlyPlan = PredefinedAccounts.getMonthlyPlanUzs(farmer.annualPlanTargetUzs)

    // State for payment method: Naqd vs Karta
    var selectedPaymentMethod by remember {
        mutableStateOf(if (isRahbar) PaymentType.CASH else PaymentType.CLICK)
    }

    // Amount input
    var amountInput by remember {
        mutableStateOf(if (monthlyPlan > 0) monthlyPlan.toString() else "1000000")
    }

    // Expense category for Rahbar in card/cash
    var expenseCategory by remember { mutableStateOf("Yoqilg'i / Solyarka") }
    var notesInput by remember { mutableStateOf("") }

    // Live clock with seconds (dd.MM.yyyy HH:mm:ss)
    var currentLiveTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        while (true) {
            currentLiveTime = sdf.format(Date())
            delay(1000L)
        }
    }

    // Receipt image picker state
    var selectedReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var receiptCheckDate by remember {
        mutableStateOf(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()))
    }
    var isOldMonthReceipt by remember { mutableStateOf(false) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedReceiptUri = uri
            // Validate check date: by default matches current month
            isOldMonthReceipt = false
        }
    }

    // Check date validation logic:
    // If the check date is from an older month, reject!
    fun validateCheckDate(dateStr: String): Boolean {
        return try {
            val parts = dateStr.trim().split(".")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt() - 1 // 0-based
                val year = parts[2].toInt()

                val nowCal = Calendar.getInstance()
                val currentMonth = nowCal.get(Calendar.MONTH)
                val currentYear = nowCal.get(Calendar.YEAR)

                // If year is past, or if same year and month < currentMonth -> OLD MONTH!
                if (year < currentYear || (year == currentYear && month < currentMonth)) {
                    isOldMonthReceipt = true
                    false
                } else {
                    isOldMonthReceipt = false
                    true
                }
            } else {
                isOldMonthReceipt = false
                true
            }
        } catch (_: Exception) {
            isOldMonthReceipt = false
            true
        }
    }

    val currentAmountLong = amountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val remainingAfterThisPayment = (currentDebtUzs - currentAmountLong).coerceAtLeast(0L)

    val currentMonthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("payment_modal_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isRahbar) "To'lov Qabul Qilish" else "To'lov Oynasi",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FarmGreenDark
                        )
                        Text(
                            text = "${farmer.name} • ${farmer.landSizeHectares} gektar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmGreenPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Yopish")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Farmer Plan & Debt Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FarmGreenPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Qolgan qarz:", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "${formatMoney(currentDebtUzs)} so'm",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentDebtUzs > 0) Color(0xFFC62828) else FarmGreenPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "1 oylik to'lov rejasi:", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "${formatMoney(monthlyPlan)} so'm/oy",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PAYMENT METHOD SELECTOR:
                // Rahbar: Naqd vs Karta
                // Dehqon: Only Karta
                Text(
                    text = "To'lov turini tanlang:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // NAQD PUL OPTION (ONLY RAHBAR)
                    if (isRahbar) {
                        val isCash = selectedPaymentMethod == PaymentType.CASH
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedPaymentMethod = PaymentType.CASH }
                                .border(
                                    width = if (isCash) 2.dp else 1.dp,
                                    color = if (isCash) FarmGreenPrimary else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (isCash) FarmGreenPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Money,
                                    contentDescription = null,
                                    tint = if (isCash) FarmGreenPrimary else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Naqd pul",
                                    fontSize = 13.sp,
                                    fontWeight = if (isCash) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCash) FarmGreenPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // KARTA OPTION (Available to both)
                    val isCard = selectedPaymentMethod != PaymentType.CASH
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (selectedPaymentMethod == PaymentType.CASH) {
                                    selectedPaymentMethod = PaymentType.CLICK
                                }
                            }
                            .border(
                                width = if (isCard) 2.dp else 1.dp,
                                color = if (isCard) FarmGreenPrimary else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isCard) FarmGreenPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (isCard) FarmGreenPrimary else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Karta (Click/Payme)",
                                fontSize = 13.sp,
                                fontWeight = if (isCard) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCard) FarmGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // If Dehqon, show info that only card is allowed
                if (!isRahbar) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dehqonlar kabinetida faqat karta orqali to'lov qabul qilinadi",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // IF KARTA: show card selector (Click, Payme, Paynet) and Farm Bank Card
                if (selectedPaymentMethod != PaymentType.CASH) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentAppChip("Click", PaymentType.CLICK, selectedPaymentMethod) { selectedPaymentMethod = it }
                        PaymentAppChip("Payme", PaymentType.PAYME, selectedPaymentMethod) { selectedPaymentMethod = it }
                        PaymentAppChip("Paynet", PaymentType.PAYNET, selectedPaymentMethod) { selectedPaymentMethod = it }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Small Bank Card helper widget
                    FarmBankCard(
                        onCopyToast = { msg ->
                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // AMOUNT INPUT & QUICK CHIPS
                // "naqd tanlasam agar bunda 1 mln yoki 1 oylik tolov yoki bulardan sal teparoqda miqdorini belgilab tolasin va tolov qoshiladi"
                Text(
                    text = "To'lov miqdorini belgilang (UZS):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Summa (so'mda)") },
                    placeholder = { Text("1 000 000") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = FarmGreenPrimary
                        )
                    },
                    supportingText = {
                        if (currentAmountLong > 0) {
                            Text(
                                text = "${formatMoney(currentAmountLong)} so'm",
                                color = FarmGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("modal_input_payment_amount"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // QUICK CHIPS: 1 mln, 1 oylik tolov, and remaining debt!
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AmountQuickChip(label = "1 000 000 so'm", amount = 1_000_000L) {
                        amountInput = "1000000"
                    }
                    if (monthlyPlan > 0) {
                        AmountQuickChip(label = "1 oylik to'lov (${formatMoney(monthlyPlan)})", amount = monthlyPlan) {
                            amountInput = monthlyPlan.toString()
                        }
                    }
                    if (currentDebtUzs > 0) {
                        AmountQuickChip(label = "Qolgan qarz (${formatMoney(currentDebtUzs)})", amount = currentDebtUzs) {
                            amountInput = currentDebtUzs.toString()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CARD PAYMENT ONLY: CHEK RASMI VA SANASINI TEKSHIRISH
                // "chunki kartadan tolov qiganida tolov chekini yuboradi va chekni sanasini tekshirishi kerak agar boshqa chek yuborsa qabul qimidi yani eski oygi tolv chekini qabil qimidi"
                if (selectedPaymentMethod != PaymentType.CASH) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "To'lov Cheki Rasmi va Sanasini Tekshirish:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pick image button
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pick_receipt_photo_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedReceiptUri != null) Icons.Default.CheckCircle else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (selectedReceiptUri != null) Color(0xFF2E7D32) else FarmGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedReceiptUri != null) "Chek rasmi tanlandi ✅ (O'zgartirish)" else "Chek rasmini yuklash (Galereyadan)",
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Check Date verification input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = receiptCheckDate,
                                    onValueChange = {
                                        receiptCheckDate = it
                                        validateCheckDate(it)
                                    },
                                    label = { Text("Chek sanasi (kun.oy.yil)") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        // Quick set to current date
                                        receiptCheckDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                                        validateCheckDate(receiptCheckDate)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                ) {
                                    Text("Bugun", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Verification banner: Reject older month receipt!
                            if (isOldMonthReceipt) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFEBEE),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "❌ Eski oydagi to'lov cheki qabul qilinmaydi! Faqat joriy oy ($currentMonthName) chekini yuklash mumkin.",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "✅ Chek sanasi joriy oyga to'g'ri keladi ($currentMonthName). Qabul qilinadi.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // RAHBAR CHIOM KATEGORIYASI
                if (isRahbar) {
                    Text(
                        text = "Chiqim / Xarajat yo'nalishi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val categories = listOf(
                        "Yoqilg'i / Solyarka",
                        "Mineral O'g'it",
                        "Traktor / Agrotexnika",
                        "Urug'lik",
                        "Sug'orish",
                        "Ish haqi",
                        "Boshqa"
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = expenseCategory == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) FarmGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { expenseCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Notes field
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Izoh / Qo'shimcha ma'lumot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Exact Live Timestamp with Seconds Display:
                // "va dexqon yoki rahabr tolov qivotganda sanasi va sogati daji sekundigacha tolagani tursin"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F8E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "To'lov vaqti (sekundigacha):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark
                            )
                        }
                        Text(
                            text = currentLiveTime.ifEmpty { "Hisoblanmoqda..." },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = FarmGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SUBMIT BUTTON
                // Disabled if amount <= 0 or if old month receipt detected
                val canSubmit = currentAmountLong > 0 && !isOldMonthReceipt

                Button(
                    onClick = {
                        onSubmitPayment(
                            selectedPaymentMethod,
                            currentAmountLong,
                            expenseCategory,
                            notesInput,
                            selectedReceiptUri?.toString()
                        )
                        onDismiss()
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_modal_payment_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmGreenPrimary,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = if (isOldMonthReceipt) {
                            "Eski oy cheki qabul qilinmaydi ❌"
                        } else {
                            "To'lovni Tasdiqlash (${formatMoney(currentAmountLong)} UZS) ✅"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentAppChip(
    label: String,
    type: PaymentType,
    selected: PaymentType,
    onSelect: (PaymentType) -> Unit
) {
    val isSel = selected == type
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSel) FarmGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect(type) }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AmountQuickChip(
    label: String,
    amount: Long,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = FarmGreenPrimary.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, FarmGreenPrimary.copy(alpha = 0.3f)),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = FarmGreenDark
        )
    }
}

private fun formatMoney(amount: Long): String {
    return DecimalFormat("#,###").format(amount).replace(",", " ")
}
