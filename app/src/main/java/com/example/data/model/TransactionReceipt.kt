package com.example.data.model

data class TransactionReceipt(
    val id: Long = 0,
    val receiptNumber: String,
    val dehqonId: String,
    val farmerName: String,
    val landSizeHectares: Double,
    val paymentType: PaymentType,
    val paymentAmountUzs: Long,
    val annualPlanTargetUzs: Long,
    val remainingDebtUzs: Long,
    val completionPercentage: Double,
    val timestamp: Long,
    val formattedDate: String,
    val referenceCode: String,
    val status: PaymentStatus,
    val farmManagerName: String = "Abdumalikov Abdurahmon",
    val farmName: String = "Ittifoq Fermer Xo'jaligi",
    val isOfflineRecorded: Boolean = false,
    val expenseCategory: String = "",
    val recordedByRole: String = "DEHQON",
    val telegramSent: Boolean = false,
    val notes: String = ""
)
