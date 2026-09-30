package com.example.data.network

import com.example.data.model.AgriculturalAdvisory
import java.util.Calendar

object OfflineAgronomyEngine {

    fun generateAdvisory(
        farmerName: String,
        landSizeHa: Double,
        annualTargetUzs: Long,
        totalPaidUzs: Long,
        remainingDebtUzs: Long,
        paymentType: String
    ): AgriculturalAdvisory {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-based: 8 = Sep, 9 = Oct

        val seasonName = when (month) {
            in 2..4 -> "Bahor (Chigit ekish va mineral oziqlantirish)"
            in 5..7 -> "Yoz (G'o'za gullash, sug'orish va parvarish)"
            in 8..10 -> "Kuz (Hosil yig'imi, kuzgi shudgor va g'alla ekish)"
            else -> "Qish (Yaxob suvi berish va texnikani ta'mirlash)"
        }

        // Calculate specific quantities for this land size
        val ammofosKg = (landSizeHa * 160).toInt()
        val kaliyKg = (landSizeHa * 80).toInt()
        val seedKg = (landSizeHa * 230).toInt()
        val waterCubicMeters = (landSizeHa * 1350).toInt()

        val completionPercent = if (annualTargetUzs > 0) {
            (totalPaidUzs.toDouble() / annualTargetUzs.toDouble()) * 100.0
        } else 100.0

        val financialAdvice = if (remainingDebtUzs <= 0) {
            "Yillik moliyaviy reja to'liq bajarildi! 100% yopiq. Fermer xo'jaligi hisobidan yangi agrotexnika va lizing xarajatlariga subsidiya ajratish imkoniyati mavjud."
        } else if (completionPercent >= 50.0) {
            "Reja ${String.format("%.1f", completionPercent)}% bajarildi. Qolgan ${formatMoney(remainingDebtUzs)} so'm qarzni hosil realizatsiyasi ($paymentType orqali) bilan noyabr oyigacha bosqichma-bosqich yopish tavsiya etiladi."
        } else {
            "Diqqat: Reja bajarilishi ${String.format("%.1f", completionPercent)}%. Agrotexnik tadbirlar (traktor yoqilg'isi, o'g'it) to'xtab qolmasligi uchun Click/Paynet orqali navbatdagi to'lovni tezlashtiring."
        }

        val recommendations = listOf(
            "Tuproq tayyorlash: ${landSizeHa} gektar yer maydonini 28-30 sm chuqurlikda shudgorlash va lazerli tekislagich yordamida nishablikni to'g'rilash.",
            "Mineral o'g'it me'yori: Kuzgi haydov ostiga jami ${ammofosKg} kg Ammofos (fosfor) va ${kaliyKg} kg Kaliy xlorid solish.",
            "Urug'lik sarfi: Kuzgi g'alla ekish uchun ${seedKg} kg saralangan va dorilangan 'Grom' yoki 'Alekseich' navli urug'lik tayyorlash.",
            "Suv tejash: Sug'orish mavsumida suv isrofgarchiligining oldini olish uchun egatlarga plyonka to'shash yoki tomchilatib sug'orish shlanglarini montaj qilish.",
            "Moliyaviy nazorat: $financialAdvice"
        )

        val nextActions = listOf(
            "1-hafta: Shudgorlash traktorlari va yoqilg'i-moylash materiallarini brigada hisobiga qabul qilish",
            "2-hafta: ${ammofosKg} kg fosforli o'g'itni maydon bo'ylab teng taqsimlab sepish",
            "3-hafta: G'alla urug'ini ekish agregatlari bilan 4-5 sm chuqurlikda ekish",
            "4-hafta: Birinchi unib chiqish suvini berish (gektariga ~${waterCubicMeters / landSizeHa.toInt().coerceAtLeast(1)} m³)"
        )

        return AgriculturalAdvisory(
            title = "Oflayn Agronom Tavsiyanomasi (${landSizeHa} ga)",
            summary = "«Ittifoq Fermer Xo'jaligi» ichki agronomik me'yorlari asosida $farmerName uchun tayyorlangan avtomatik tavsiyalar to'plami. Dalada internet yo'qligida ham hisob-kitoblar to'liq ishlaydi.",
            recommendations = recommendations,
            nextActions = nextActions,
            fertilizerSchedule = "Fosfor (Ammofos): ${ammofosKg} kg | Kaliy: ${kaliyKg} kg | Selitra (bahorda): ${(landSizeHa * 250).toInt()} kg",
            waterManagement = "Kuzgi/Qishki yaxob suvi: jami ${waterCubicMeters} m³ (har bir gektarga 1,300-1,400 m³ me'yor)",
            seasonNotice = seasonName,
            isOfflineEngine = true
        )
    }

    private fun formatMoney(amount: Long): String {
        return "%,d".format(amount).replace(',', ' ')
    }
}
