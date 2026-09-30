package com.example.data.network

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.TransactionReceipt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TelegramBotService(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("telegram_farm_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // Default configuration (can be updated from Rahbar settings panel)
    var botToken: String
        get() = prefs.getString("tg_bot_token", "8134592814:AAH_IttifoqFarmReceiptBot") ?: ""
        set(value) = prefs.edit().putString("tg_bot_token", value.trim()).apply()

    var chatId: String
        get() = prefs.getString("tg_chat_id", "-100245892104") ?: "" // Farm receipts group or manager ID
        set(value) = prefs.edit().putString("tg_chat_id", value.trim()).apply()

    suspend fun sendPaymentReceiptToBot(receipt: TransactionReceipt): Result<String> = withContext(Dispatchers.IO) {
        val currentToken = botToken
        val currentChat = chatId

        if (currentToken.isBlank() || currentChat.isBlank()) {
            return@withContext Result.failure(Exception("Telegram Bot token yoki Chat ID belgilanmagan"))
        }

        val messageText = buildTelegramReceiptHtml(receipt)

        val jsonBody = JSONObject().apply {
            put("chat_id", currentChat)
            put("text", messageText)
            put("parse_mode", "HTML")
            put("disable_web_page_preview", true)
        }

        try {
            val url = "https://api.telegram.org/bot$currentToken/sendMessage"
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Result.success("Chek Telegram botga muvaffaqiyatli jo'natildi!")
            } else {
                val errorMsg = try {
                    JSONObject(responseString).optString("description", "Telegram xatosi: ${response.code}")
                } catch (_: Exception) {
                    "Telegram HTTP xatosi ${response.code}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildTelegramReceiptHtml(receipt: TransactionReceipt): String {
        val expenseLine = if (receipt.expenseCategory.isNotBlank()) {
            "🏷 <b>Chiqim / Xarajat:</b> ${receipt.expenseCategory}\n"
        } else ""

        val notesLine = if (receipt.notes.isNotBlank()) {
            "📝 <b>Izoh:</b> ${receipt.notes}\n"
        } else ""

        val recordedBy = if (receipt.recordedByRole == "RAHBAR") {
            "👑 Rahbar tomonidan qayd etildi"
        } else {
            "📱 Dehqon shaxsiy kabinetidan yuborildi"
        }

        return """
🌾 <b>«ITTIFOQ FERMER XO'JALIGI» TO'LOV CHEKI</b>
━━━━━━━━━━━━━━━━━━━━
📄 <b>Chek raqami:</b> <code>${receipt.receiptNumber}</code>
👤 <b>Dehqon:</b> <b>${receipt.farmerName}</b> (ID: <code>${receipt.dehqonId}</code>)
🚜 <b>Yer maydoni:</b> ${receipt.landSizeHectares} gektar
💳 <b>To'lov usuli:</b> ${receipt.paymentType.title} (${receipt.paymentType.subtitle})
💰 <b>To'langan summa:</b> <b>${formatMoney(receipt.paymentAmountUzs)} UZS</b>
🎯 <b>Yillik reja marrasi:</b> ${formatMoney(receipt.annualPlanTargetUzs)} UZS
📉 <b>Qolgan qarzdorlik:</b> <b>${formatMoney(receipt.remainingDebtUzs)} UZS</b>
📊 <b>Reja bajarilishi:</b> ${String.format("%.1f", receipt.completionPercentage)}%
🏷 <b>Fiskal kod:</b> <code>${receipt.referenceCode}</code>
⏰ <b>Sana va vaqt:</b> ${receipt.formattedDate}
$expenseLine$notesLine━━━━━━━━━━━━━━━━━━━━
✅ <b>Holati:</b> RASMIY TASDIQLANDI
📌 <b>Manba:</b> $recordedBy
✍️ <b>Mas'ul rahbar:</b> Abdumalikov Abdurahmon
        """.trimIndent()
    }

    private fun formatMoney(amount: Long): String {
        return "%,d".format(amount).replace(',', ' ')
    }
}
