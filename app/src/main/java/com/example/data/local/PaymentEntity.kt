package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String,
    val dehqonId: String,
    val farmerName: String,
    val landSizeHectares: Double,
    val paymentType: String, // CASH, CLICK, PAYNET, PAYME
    val paymentAmountUzs: Long,
    val annualPlanTargetUzs: Long,
    val remainingDebtUzs: Long,
    val completionPercentage: Double,
    val timestamp: Long,
    val formattedDate: String,
    val referenceCode: String,
    val status: String,
    val isOfflineRecorded: Boolean = false,
    val expenseCategory: String = "", // Rahbar uchun chiqim/xarajat turi
    val recordedByRole: String = "DEHQON", // "RAHBAR" or "DEHQON"
    val telegramSent: Boolean = false,
    val notes: String = ""
)
