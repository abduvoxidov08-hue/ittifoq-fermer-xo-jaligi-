package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

enum class PaymentStatus(
    val labelUz: String,
    val color: Color
) {
    CONFIRMED("Rasmiy Tasdiqlangan", StatusSuccess),
    PARTIAL("Qisman To'langan", StatusWarning),
    PENDING("Kutilmoqda", StatusError);

    companion object {
        fun fromString(value: String): PaymentStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CONFIRMED
        }
    }
}
