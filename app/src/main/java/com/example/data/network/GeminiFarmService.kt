package com.example.data.network

import com.example.BuildConfig
import com.example.data.model.AgriculturalAdvisory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiFarmService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun requestAdvisory(
        farmerName: String,
        dehqonId: String,
        landSizeHa: Double,
        annualTargetUzs: Long,
        totalPaidUzs: Long,
        remainingDebtUzs: Long,
        paymentType: String,
        forceOffline: Boolean = false
    ): AgriculturalAdvisory = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If manual offline or missing API key, use rich offline engine directly
        if (forceOffline || apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext OfflineAgronomyEngine.generateAdvisory(
                farmerName = farmerName,
                landSizeHa = landSizeHa,
                annualTargetUzs = annualTargetUzs,
                totalPaidUzs = totalPaidUzs,
                remainingDebtUzs = remainingDebtUzs,
                paymentType = paymentType
            )
        }

        val prompt = """
            Siz O'zbekistondagi "Ittifoq Fermer Xo'jaligi" bosh agronomi va qishloq xo'jaligi moliyaviy maslahatchisisiz.
            Fermer xo'jaligi rahbari: Abdumalikov Abdurahmon.
            Dehqon ma'lumotlari:
            - Ismi / ID: $farmerName ($dehqonId)
            - Ekin maydoni: $landSizeHa gektar
            - Yillik reja: ${formatMoney(annualTargetUzs)} so'm
            - Jami to'langan summa: ${formatMoney(totalPaidUzs)} so'm
            - Qolgan qarzdorlik: ${formatMoney(remainingDebtUzs)} so'm
            - Oxirgi to'lov usuli: $paymentType

            Hozirgi mavsumiy davr (kuzgi yig'im-terim, kuzgi shudgor va g'alla ekish) uchun dehqonga aniq, raqamlarga asoslangan tavsiyanoma tuzing.
            Quyidagi JSON formatda javob bering (faqat JSON qaytaring, boshqa so'z qo'shmang):
            {
              "title": "Sun'iy Intellekt Agronom Tavsiyasi ($landSizeHa ga)",
              "summary": "Qisqa 2 jumlali xulosa",
              "recommendations": [
                "Yerga ishlov berish va shudgorlash tavsiyasi",
                "Mineral o'g'it me'yori (gektariga hisoblangan kg)",
                "Urug'lik sarfi va nav tanlash",
                "Suv tejash va sug'orish rejasi",
                "Moliyaviy intizom va qarzni yopish bo'yicha maslahat"
              ],
              "nextActions": [
                "1-qadam...",
                "2-qadam...",
                "3-qadam...",
                "4-qadam..."
              ],
              "fertilizerSchedule": "O'g'it jadvali...",
              "waterManagement": "Suv berish rejasi...",
              "seasonNotice": "Mavsumiy eslatma..."
            }
        """.trimIndent()

        try {
            val jsonRequest = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                    put("responseMimeType", "application/json")
                })
            }

            val requestBody = jsonRequest.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                // Remote API failed or quota exceeded -> seamless fallback
                return@withContext OfflineAgronomyEngine.generateAdvisory(
                    farmerName = farmerName,
                    landSizeHa = landSizeHa,
                    annualTargetUzs = annualTargetUzs,
                    totalPaidUzs = totalPaidUzs,
                    remainingDebtUzs = remainingDebtUzs,
                    paymentType = paymentType
                )
            }

            val responseString = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (rawText.isBlank()) {
                return@withContext OfflineAgronomyEngine.generateAdvisory(
                    farmerName = farmerName,
                    landSizeHa = landSizeHa,
                    annualTargetUzs = annualTargetUzs,
                    totalPaidUzs = totalPaidUzs,
                    remainingDebtUzs = remainingDebtUzs,
                    paymentType = paymentType
                )
            }

            // Clean markdown code blocks if any
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanJson)

            val recsArray = parsed.optJSONArray("recommendations")
            val recsList = mutableListOf<String>()
            if (recsArray != null) {
                for (i in 0 until recsArray.length()) {
                    recsList.add(recsArray.getString(i))
                }
            }

            val actionsArray = parsed.optJSONArray("nextActions")
            val actionsList = mutableListOf<String>()
            if (actionsArray != null) {
                for (i in 0 until actionsArray.length()) {
                    actionsList.add(actionsArray.getString(i))
                }
            }

            AgriculturalAdvisory(
                title = parsed.optString("title", "Gemini 3.5 Flash Agronom Tavsiyasi"),
                summary = parsed.optString("summary", "Abdumalikov Abdurahmon fermer xo'jaligi uchun tavsiyalar tayyorlandi."),
                recommendations = if (recsList.isNotEmpty()) recsList else listOf("Yerga ishlov berish rejasini amalga oshirish"),
                nextActions = if (actionsList.isNotEmpty()) actionsList else listOf("Agrotexnika taqsimoti"),
                fertilizerSchedule = parsed.optString("fertilizerSchedule", "Standart fosfor va azot me'yori"),
                waterManagement = parsed.optString("waterManagement", "Kuzgi shudgor oldi sug'orish"),
                seasonNotice = parsed.optString("seasonNotice", "Kuzgi dala ishlari"),
                isOfflineEngine = false
            )
        } catch (_: Exception) {
            // Any network or parsing issue -> graceful offline fallback
            OfflineAgronomyEngine.generateAdvisory(
                farmerName = farmerName,
                landSizeHa = landSizeHa,
                annualTargetUzs = annualTargetUzs,
                totalPaidUzs = totalPaidUzs,
                remainingDebtUzs = remainingDebtUzs,
                paymentType = paymentType
            )
        }
    }

    private fun formatMoney(amount: Long): String {
        return "%,d".format(amount).replace(',', ' ')
    }
}
