package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CashOrange
import com.example.ui.theme.CashOrangeContainer
import com.example.ui.theme.ClickBlue
import com.example.ui.theme.ClickBlueContainer
import com.example.ui.theme.PaymeTeal
import com.example.ui.theme.PaymeTealContainer
import com.example.ui.theme.PaynetGreen
import com.example.ui.theme.PaynetGreenContainer

enum class PaymentType(
    val title: String,
    val subtitle: String,
    val brandColor: Color,
    val containerColor: Color,
    val codePrefix: String
) {
    CASH(
        title = "Naqd pul",
        subtitle = "G'azna kassa qabuli",
        brandColor = CashOrange,
        containerColor = CashOrangeContainer,
        codePrefix = "CSH"
    ),
    CLICK(
        title = "Click Up",
        subtitle = "Elektron karta o'tkazmasi",
        brandColor = ClickBlue,
        containerColor = ClickBlueContainer,
        codePrefix = "CLK"
    ),
    PAYNET(
        title = "Paynet",
        subtitle = "Kassa / Terminal to'lovi",
        brandColor = PaynetGreen,
        containerColor = PaynetGreenContainer,
        codePrefix = "PNT"
    ),
    PAYME(
        title = "Payme",
        subtitle = "Biznes / Karta to'lovi",
        brandColor = PaymeTeal,
        containerColor = PaymeTealContainer,
        codePrefix = "PYM"
    );

    companion object {
        fun fromString(value: String): PaymentType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CASH
        }
    }
}
