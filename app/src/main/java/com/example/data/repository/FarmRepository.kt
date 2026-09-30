package com.example.data.repository

import com.example.data.local.AdvisoryCacheEntity
import com.example.data.local.FarmDao
import com.example.data.local.FarmerEntity
import com.example.data.local.PaymentEntity
import com.example.data.model.AgriculturalAdvisory
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentType
import com.example.data.model.TransactionReceipt
import com.example.data.network.GeminiFarmService
import com.example.data.network.TelegramBotService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class FarmRepository(
    private val dao: FarmDao,
    private val geminiService: GeminiFarmService,
    private val telegramBotService: TelegramBotService
) {
    val allFarmers: Flow<List<FarmerEntity>> = dao.getAllFarmers()
    val allPayments: Flow<List<TransactionReceipt>> = dao.getAllPayments().map { entities ->
        entities.map { it.toModel() }
    }

    fun getPaymentsForFarmer(dehqonId: String): Flow<List<TransactionReceipt>> {
        return dao.getPaymentsForFarmer(dehqonId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getTotalPaidForFarmer(dehqonId: String): Flow<Long> {
        return dao.getTotalPaidForFarmer(dehqonId).map { it ?: 0L }
    }

    suspend fun getFarmerByDehqonId(dehqonId: String): FarmerEntity? {
        return dao.getFarmerByDehqonId(dehqonId)
    }

    suspend fun getFarmerByUsername(username: String): FarmerEntity? {
        return dao.findFarmerByUsername(username)
    }

    suspend fun authenticate(username: String, password: String): FarmerEntity? {
        return dao.authenticate(username.trim(), password.trim())
    }

    suspend fun registerFarmer(
        username: String,
        password: String,
        name: String,
        landSizeHectares: Double,
        annualPlanTargetUzs: Long,
        phone: String = "+998 90 000 00 00",
        cropType: String = "Bug'doy va sabzavot"
    ): Result<FarmerEntity> {
        val existing = dao.findFarmerByUsername(username.trim())
        if (existing != null) {
            return Result.failure(Exception("Ushbu login band. Boshqa login tanlang!"))
        }

        val dehqonId = "IFX-2026-${(100..999).random()}"
        val entity = FarmerEntity(
            username = username.trim(),
            password = password.trim(),
            role = "DEHQON",
            dehqonId = dehqonId,
            name = name.trim(),
            landSizeHectares = landSizeHectares,
            annualPlanTargetUzs = annualPlanTargetUzs,
            phone = phone,
            cropType = cropType
        )

        val id = dao.insertFarmer(entity)
        return Result.success(entity.copy(id = id))
    }

    suspend fun saveFarmer(farmer: FarmerEntity): Long {
        return dao.insertFarmer(farmer)
    }

    /**
     * Records payment and automatically sends receipt to Telegram Bot!
     */
    suspend fun recordPayment(
        dehqonId: String,
        farmerName: String,
        landSizeHectares: Double,
        paymentType: PaymentType,
        paymentAmountUzs: Long,
        annualPlanTargetUzs: Long,
        isOfflineMode: Boolean,
        expenseCategory: String = "",
        recordedByRole: String = "DEHQON",
        notes: String = ""
    ): TransactionReceipt {
        val now = System.currentTimeMillis()
        val locale = Locale.Builder().setLanguage("uz").setRegion("UZ").build()
        val dateFormat = SimpleDateFormat("dd-MMMM, yyyy HH:mm", locale)
        val formattedDate = dateFormat.format(Date(now))

        val randomNum = Random().nextInt(9000) + 1000
        val dayMonthCode = SimpleDateFormat("MMdd", Locale.US).format(Date(now))
        val receiptNumber = "IFX-2026-$dayMonthCode-$randomNum"

        val refCode = "${paymentType.codePrefix}-$randomNum-UZ-${Random().nextInt(900) + 100}"

        // Step 2 logic:
        // Calculate remaining balance: (Target Amount - Payment Amount)
        val remainingDebt = (annualPlanTargetUzs - paymentAmountUzs).coerceAtLeast(0L)

        // Calculate completion percentage: (Payment Amount / Target Amount) * 100%
        val completionPercentage = if (annualPlanTargetUzs > 0) {
            (paymentAmountUzs.toDouble() / annualPlanTargetUzs.toDouble()) * 100.0
        } else {
            100.0
        }

        val status = if (remainingDebt == 0L) {
            PaymentStatus.CONFIRMED
        } else if (completionPercentage >= 50.0) {
            PaymentStatus.CONFIRMED
        } else {
            PaymentStatus.PARTIAL
        }

        val entity = PaymentEntity(
            receiptNumber = receiptNumber,
            dehqonId = dehqonId,
            farmerName = farmerName,
            landSizeHectares = landSizeHectares,
            paymentType = paymentType.name,
            paymentAmountUzs = paymentAmountUzs,
            annualPlanTargetUzs = annualPlanTargetUzs,
            remainingDebtUzs = remainingDebt,
            completionPercentage = completionPercentage,
            timestamp = now,
            formattedDate = formattedDate,
            referenceCode = refCode,
            status = status.name,
            isOfflineRecorded = isOfflineMode,
            expenseCategory = expenseCategory,
            recordedByRole = recordedByRole,
            telegramSent = false,
            notes = notes
        )

        val insertedId = dao.insertPayment(entity)

        // Ensure farmer exists/updated
        val existingFarmer = dao.getFarmerByDehqonId(dehqonId)
        if (existingFarmer == null) {
            dao.insertFarmer(
                FarmerEntity(
                    dehqonId = dehqonId,
                    name = farmerName,
                    landSizeHectares = landSizeHectares,
                    annualPlanTargetUzs = annualPlanTargetUzs
                )
            )
        }

        var model = entity.copy(id = insertedId).toModel()

        // AUTOMATIC TELEGRAM BOT SUBMISSION
        // "u yuborgan dexqoning malimoti yani qancha tolagani va chekini telegramga yuborishi kereak u odam jonatmidi ilovadan botgaa yuborishi kere"
        if (!isOfflineMode) {
            val tgResult = telegramBotService.sendPaymentReceiptToBot(model)
            if (tgResult.isSuccess) {
                dao.updatePaymentTelegramStatus(insertedId, true)
                model = model.copy(telegramSent = true)
            }
        }

        return model
    }

    suspend fun getAgriculturalAdvisory(
        farmerName: String,
        dehqonId: String,
        landSizeHa: Double,
        annualTargetUzs: Long,
        totalPaidUzs: Long,
        remainingDebtUzs: Long,
        paymentType: String,
        forceOffline: Boolean
    ): AgriculturalAdvisory {
        val advisory = geminiService.requestAdvisory(
            farmerName = farmerName,
            dehqonId = dehqonId,
            landSizeHa = landSizeHa,
            annualTargetUzs = annualTargetUzs,
            totalPaidUzs = totalPaidUzs,
            remainingDebtUzs = remainingDebtUzs,
            paymentType = paymentType,
            forceOffline = forceOffline
        )

        try {
            val cacheEntity = AdvisoryCacheEntity(
                dehqonId = dehqonId,
                title = advisory.title,
                summary = advisory.summary,
                recommendationsJson = JSONArray(advisory.recommendations).toString(),
                nextActionsJson = JSONArray(advisory.nextActions).toString(),
                fertilizerSchedule = advisory.fertilizerSchedule,
                waterManagement = advisory.waterManagement,
                seasonNotice = advisory.seasonNotice,
                isOfflineEngine = advisory.isOfflineEngine
            )
            dao.saveAdvisoryCache(cacheEntity)
        } catch (_: Exception) {}

        return advisory
    }

    var tgBotToken: String
        get() = telegramBotService.botToken
        set(value) { telegramBotService.botToken = value }

    var tgChatId: String
        get() = telegramBotService.chatId
        set(value) { telegramBotService.chatId = value }

    private fun PaymentEntity.toModel(): TransactionReceipt {
        return TransactionReceipt(
            id = id,
            receiptNumber = receiptNumber,
            dehqonId = dehqonId,
            farmerName = farmerName,
            landSizeHectares = landSizeHectares,
            paymentType = PaymentType.fromString(paymentType),
            paymentAmountUzs = paymentAmountUzs,
            annualPlanTargetUzs = annualPlanTargetUzs,
            remainingDebtUzs = remainingDebtUzs,
            completionPercentage = completionPercentage,
            timestamp = timestamp,
            formattedDate = formattedDate,
            referenceCode = referenceCode,
            status = PaymentStatus.fromString(status),
            farmManagerName = "Abdumalikov Abdurahmon",
            farmName = "Ittifoq Fermer Xo'jaligi",
            isOfflineRecorded = isOfflineRecorded,
            expenseCategory = expenseCategory,
            recordedByRole = recordedByRole,
            telegramSent = telegramSent,
            notes = notes
        )
    }
}
