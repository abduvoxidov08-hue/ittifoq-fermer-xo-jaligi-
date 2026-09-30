package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FarmerEntity
import com.example.data.model.AgriculturalAdvisory
import com.example.data.model.PaymentType
import com.example.data.model.TransactionReceipt
import com.example.data.network.NetworkMonitor
import com.example.data.network.OfflineAgronomyEngine
import com.example.data.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FarmUiState(
    // Authentication
    val currentUser: FarmerEntity? = null,
    val loginUsernameInput: String = "",
    val loginPasswordInput: String = "",
    val loginError: String? = null,
    val isAuthenticating: Boolean = false,

    // Current Active Farmer context
    val currentDehqonId: String = "IFX-2026-001",
    val currentFarmerName: String = "Abdumalikov Abdurahmon",
    val landSizeHectares: Double = 25.0,
    val landSizeInput: String = "25.0",
    val annualPlanTargetUzs: Long = 75_000_000L,
    val annualPlanInput: String = "75000000",
    val paymentAmountUzs: Long = 10_000_000L,
    val paymentAmountInput: String = "10000000",
    val selectedPaymentType: PaymentType = PaymentType.CLICK,
    val notesInput: String = "",

    // Rahbar specific expense category
    val selectedExpenseCategory: String = "Yoqilg'i / Solyarka",

    // Step 2 Calculation states
    val calculatedRemainingBalance: Long = 65_000_000L,
    val calculatedCompletionPercentage: Double = 13.33,

    // Cumulative stats for current farmer
    val cumulativePaidAmount: Long = 0L,
    val cumulativeRemainingDebt: Long = 75_000_000L,
    val cumulativePercentage: Double = 0.0,

    // Receipt & Dialogs
    val activeReceipt: TransactionReceipt? = null,
    val showReceiptDialog: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val showTelegramSettingsDialog: Boolean = false,
    val showPaymentModal: Boolean = false,
    val paymentModalFarmer: FarmerEntity? = null,
    val paymentModalFarmerDebt: Long = 0L,
    val telegramBotTokenInput: String = "",
    val telegramChatIdInput: String = "",

    // Advisory (Step 2 & Step 3)
    val advisory: AgriculturalAdvisory? = null,
    val isLoadingAdvisory: Boolean = false,

    // Network / Offline status
    val isOnline: Boolean = true,
    val isManualOffline: Boolean = false,

    // UI Navigation & Filters
    val currentTab: Int = 0, // 0: Kabinet / Boshqaruv, 1: AI Agronom, 2: Cheklar
    val historyFilter: PaymentType? = null,
    val toastMessage: String? = null
) {
    val isRahbar: Boolean
        get() = currentUser?.role == "RAHBAR"
}

class FarmViewModel(
    private val repository: FarmRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FarmUiState(
            telegramBotTokenInput = repository.tgBotToken,
            telegramChatIdInput = repository.tgChatId
        )
    )
    val uiState: StateFlow<FarmUiState> = _uiState.asStateFlow()

    val allFarmers: StateFlow<List<FarmerEntity>> = repository.allFarmers
        .map { list ->
            if (list.size < com.example.data.local.PredefinedAccounts.ALL_ACCOUNTS.size) {
                val byUser = list.associateBy { it.username.lowercase() }
                com.example.data.local.PredefinedAccounts.ALL_ACCOUNTS.map { predefined ->
                    byUser[predefined.username.lowercase()] ?: predefined
                }
            } else {
                list
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.example.data.local.PredefinedAccounts.ALL_ACCOUNTS
        )

    val allPayments: StateFlow<List<TransactionReceipt>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Ensure all predefined farmers are persisted in local DB
        viewModelScope.launch {
            repository.ensureAllPredefinedFarmers()
        }

        // Monitor network state
        viewModelScope.launch {
            combine(networkMonitor.isOnline, networkMonitor.isManualOffline) { online, manualOffline ->
                Pair(online, manualOffline)
            }.collect { (online, manualOffline) ->
                _uiState.update {
                    it.copy(isOnline = online, isManualOffline = manualOffline)
                }
            }
        }

        // Monitor payments to recalculate cumulative totals
        viewModelScope.launch {
            allPayments.collect { payments ->
                recalculateCumulativeTotals(payments)
            }
        }
    }

    // --- AUTHENTICATION & REGISTRATION ---

    fun onLoginUsernameChanged(username: String) {
        _uiState.update { it.copy(loginUsernameInput = username, loginError = null) }
    }

    fun onLoginPasswordChanged(password: String) {
        _uiState.update { it.copy(loginPasswordInput = password, loginError = null) }
    }

    fun quickSelectLoginAccount(username: String, pass: String) {
        _uiState.update {
            it.copy(loginUsernameInput = username, loginPasswordInput = pass, loginError = null)
        }
        login()
    }

    fun login() {
        val username = _uiState.value.loginUsernameInput.replace("\u00A0", " ").trim()
        val password = _uiState.value.loginPasswordInput.replace("\u00A0", " ").trim()

        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginError = "Login va parolni kiriting") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthenticating = true, loginError = null) }
            val farmer = repository.authenticate(username, password)
            if (farmer != null) {
                _uiState.update {
                    it.copy(
                        currentUser = farmer,
                        isAuthenticating = false,
                        loginError = null,
                        currentDehqonId = farmer.dehqonId,
                        currentFarmerName = farmer.name,
                        landSizeHectares = farmer.landSizeHectares,
                        landSizeInput = farmer.landSizeHectares.toString(),
                        annualPlanTargetUzs = farmer.annualPlanTargetUzs,
                        annualPlanInput = farmer.annualPlanTargetUzs.toString(),
                        // Dehqons can only use cards!
                        selectedPaymentType = PaymentType.CLICK,
                        currentTab = 0
                    )
                }
                recalculateCumulativeTotals(allPayments.value)
                refreshAdvisory()
                showToast("Xush kelibsiz, ${farmer.name}!")
            } else {
                _uiState.update {
                    it.copy(
                        isAuthenticating = false,
                        loginError = "Login yoki parol noto'g'ri. Iltimos qayta tekshiring!"
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                loginUsernameInput = "",
                loginPasswordInput = "",
                loginError = null,
                currentTab = 0
            )
        }
        showToast("Tizimdan chiqildi")
    }

    // --- FARM CALCULATIONS & FORM LOGIC ---

    private fun recalculateCumulativeTotals(payments: List<TransactionReceipt>) {
        val currentId = _uiState.value.currentDehqonId
        val farmerPayments = payments.filter { it.dehqonId == currentId }
        val totalPaid = farmerPayments.sumOf { it.paymentAmountUzs }
        val target = _uiState.value.annualPlanTargetUzs

        val cumulativeRemaining = (target - totalPaid).coerceAtLeast(0L)
        val cumulativePct = if (target > 0) {
            (totalPaid.toDouble() / target.toDouble()) * 100.0
        } else 100.0

        _uiState.update {
            it.copy(
                cumulativePaidAmount = totalPaid,
                cumulativeRemainingDebt = cumulativeRemaining,
                cumulativePercentage = cumulativePct
            )
        }
    }

    fun onFarmerSelected(farmer: FarmerEntity) {
        val newTarget = farmer.annualPlanTargetUzs
        val newLand = farmer.landSizeHectares
        val paymentAmount = _uiState.value.paymentAmountUzs

        val remBalance = (newTarget - paymentAmount).coerceAtLeast(0L)
        val compPct = if (newTarget > 0) (paymentAmount.toDouble() / newTarget.toDouble()) * 100.0 else 0.0

        _uiState.update {
            it.copy(
                currentDehqonId = farmer.dehqonId,
                currentFarmerName = farmer.name,
                landSizeHectares = newLand,
                landSizeInput = newLand.toString(),
                annualPlanTargetUzs = newTarget,
                annualPlanInput = newTarget.toString(),
                calculatedRemainingBalance = remBalance,
                calculatedCompletionPercentage = compPct
            )
        }
        recalculateCumulativeTotals(allPayments.value)
        refreshAdvisory()
    }

    fun onFarmerNameInputChanged(nameOrId: String) {
        val matchedFarmer = allFarmers.value.firstOrNull {
            it.dehqonId.equals(nameOrId.trim(), ignoreCase = true) ||
                    it.name.contains(nameOrId.trim(), ignoreCase = true)
        }
        if (matchedFarmer != null) {
            onFarmerSelected(matchedFarmer)
        } else {
            _uiState.update { it.copy(currentFarmerName = nameOrId) }
        }
    }

    fun onLandSizeChanged(input: String) {
        val cleaned = input.filter { it.isDigit() || it == '.' }
        val parsed = cleaned.toDoubleOrNull() ?: _uiState.value.landSizeHectares
        _uiState.update {
            it.copy(
                landSizeInput = input,
                landSizeHectares = parsed
            )
        }
    }

    fun onAnnualPlanTargetChanged(input: String) {
        val digits = input.filter { it.isDigit() }
        val parsed = digits.toLongOrNull() ?: 0L
        val currentPayment = _uiState.value.paymentAmountUzs

        val remaining = (parsed - currentPayment).coerceAtLeast(0L)
        val percent = if (parsed > 0) (currentPayment.toDouble() / parsed.toDouble()) * 100.0 else 0.0

        _uiState.update {
            it.copy(
                annualPlanInput = digits,
                annualPlanTargetUzs = parsed,
                calculatedRemainingBalance = remaining,
                calculatedCompletionPercentage = percent
            )
        }
        recalculateCumulativeTotals(allPayments.value)
    }

    fun onPaymentAmountChanged(input: String) {
        val digits = input.filter { it.isDigit() }
        val parsed = digits.toLongOrNull() ?: 0L
        val target = _uiState.value.annualPlanTargetUzs

        val remaining = (target - parsed).coerceAtLeast(0L)
        val percent = if (target > 0) (parsed.toDouble() / target.toDouble()) * 100.0 else 0.0

        _uiState.update {
            it.copy(
                paymentAmountInput = digits,
                paymentAmountUzs = parsed,
                calculatedRemainingBalance = remaining,
                calculatedCompletionPercentage = percent
            )
        }
    }

    fun onSelectPresetAmount(amount: Long) {
        onPaymentAmountChanged(amount.toString())
    }

    fun onPaymentTypeChanged(type: PaymentType) {
        // Enforce constraint: Dehqons cannot select Cash!
        if (!_uiState.value.isRahbar && type == PaymentType.CASH) {
            showToast("Dehqonlar faqat karta orqali to'lov qila oladi!")
            return
        }
        _uiState.update { it.copy(selectedPaymentType = type) }
    }

    fun onExpenseCategoryChanged(category: String) {
        _uiState.update { it.copy(selectedExpenseCategory = category) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notesInput = notes) }
    }

    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    fun setHistoryFilter(filter: PaymentType?) {
        _uiState.update { it.copy(historyFilter = filter) }
    }

    fun toggleOfflineMode() {
        networkMonitor.toggleManualOfflineMode()
        val newState = !networkMonitor.isManualOffline.value
        showToast(if (newState) "Oflayn rejim faollashtirildi (Dalada ishlash)" else "Avtomatik tarmoq rejimi yoqildi")
    }

    // --- TELEGRAM BOT SETTINGS ---

    fun openTelegramSettings() {
        _uiState.update {
            it.copy(
                showTelegramSettingsDialog = true,
                telegramBotTokenInput = repository.tgBotToken,
                telegramChatIdInput = repository.tgChatId
            )
        }
    }

    fun closeTelegramSettings() {
        _uiState.update { it.copy(showTelegramSettingsDialog = false) }
    }

    fun onTgBotTokenChanged(v: String) = _uiState.update { it.copy(telegramBotTokenInput = v) }
    fun onTgChatIdChanged(v: String) = _uiState.update { it.copy(telegramChatIdInput = v) }

    fun saveTelegramSettings() {
        repository.tgBotToken = _uiState.value.telegramBotTokenInput
        repository.tgChatId = _uiState.value.telegramChatIdInput
        closeTelegramSettings()
        showToast("Telegram Bot sozlamalari saqlandi!")
    }

    // --- PAYMENT SUBMISSION & BOT NOTIFICATION ---

    fun submitPayment() {
        val state = _uiState.value
        if (state.paymentAmountUzs <= 0) {
            showToast("Iltimos, to'lov summasini kiriting")
            return
        }

        // Validate dehqon payment constraint
        if (!state.isRahbar && state.selectedPaymentType == PaymentType.CASH) {
            showToast("Dehqonlar o'z hisobiga naqd qo'sha olmaydi, faqat karta orqali to'lov qilinadi!")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            try {
                val receipt = repository.recordPayment(
                    dehqonId = state.currentDehqonId,
                    farmerName = state.currentFarmerName,
                    landSizeHectares = state.landSizeHectares,
                    paymentType = state.selectedPaymentType,
                    paymentAmountUzs = state.paymentAmountUzs,
                    annualPlanTargetUzs = state.annualPlanTargetUzs,
                    isOfflineMode = !state.isOnline,
                    expenseCategory = if (state.isRahbar) state.selectedExpenseCategory else "",
                    recordedByRole = if (state.isRahbar) "RAHBAR" else "DEHQON",
                    notes = state.notesInput
                )

                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        activeReceipt = receipt,
                        showReceiptDialog = true,
                        notesInput = ""
                    )
                }

                refreshAdvisory()

                val tgMsg = if (receipt.telegramSent) {
                    "To'lov qabul qilindi va chek Telegram Botga yuborildi! ✅"
                } else if (!state.isOnline) {
                    "To'lov oflayn saqlandi (Tarmoqqa ulanishda botga yuboriladi) 📥"
                } else {
                    "To'lov qabul qilindi va chek shakllantirildi! 📄"
                }
                showToast(tgMsg)
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingPayment = false) }
                showToast("Xatolik: ${e.message}")
            }
        }
    }

    fun openPaymentModal(farmer: FarmerEntity, currentDebt: Long) {
        onFarmerSelected(farmer)
        _uiState.update {
            it.copy(
                showPaymentModal = true,
                paymentModalFarmer = farmer,
                paymentModalFarmerDebt = currentDebt
            )
        }
    }

    fun closePaymentModal() {
        _uiState.update {
            it.copy(
                showPaymentModal = false,
                paymentModalFarmer = null
            )
        }
    }

    fun submitModalPayment(
        paymentType: PaymentType,
        amount: Long,
        expenseCategory: String,
        notes: String,
        receiptUri: String?
    ) {
        val state = _uiState.value
        val farmer = state.paymentModalFarmer ?: state.currentUser ?: return
        if (amount <= 0) {
            showToast("Iltimos, to'lov summasini kiriting")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            try {
                val receipt = repository.recordPayment(
                    dehqonId = farmer.dehqonId,
                    farmerName = farmer.name,
                    landSizeHectares = farmer.landSizeHectares,
                    paymentType = paymentType,
                    paymentAmountUzs = amount,
                    annualPlanTargetUzs = farmer.annualPlanTargetUzs,
                    isOfflineMode = !state.isOnline,
                    expenseCategory = if (state.isRahbar) expenseCategory else "",
                    recordedByRole = if (state.isRahbar) "RAHBAR" else "DEHQON",
                    notes = notes
                )

                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        showPaymentModal = false,
                        paymentModalFarmer = null,
                        activeReceipt = receipt,
                        showReceiptDialog = true
                    )
                }

                refreshAdvisory()

                val tgMsg = if (receipt.telegramSent) {
                    "To'lov qabul qilindi va chek Telegram Botga yuborildi! ✅"
                } else if (!state.isOnline) {
                    "To'lov oflayn saqlandi (Tarmoqqa ulanishda botga yuboriladi) 📥"
                } else {
                    "To'lov muvaffaqiyatli qabul qilindi! 📄"
                }
                showToast(tgMsg)
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingPayment = false) }
                showToast("Xatolik: ${e.message}")
            }
        }
    }

    fun refreshAdvisory() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAdvisory = true) }
            val advisory = try {
                repository.getAgriculturalAdvisory(
                    farmerName = state.currentFarmerName,
                    dehqonId = state.currentDehqonId,
                    landSizeHa = state.landSizeHectares,
                    annualTargetUzs = state.annualPlanTargetUzs,
                    totalPaidUzs = state.cumulativePaidAmount,
                    remainingDebtUzs = state.cumulativeRemainingDebt,
                    paymentType = state.selectedPaymentType.title,
                    forceOffline = !state.isOnline
                )
            } catch (_: Exception) {
                OfflineAgronomyEngine.generateAdvisory(
                    farmerName = state.currentFarmerName,
                    landSizeHa = state.landSizeHectares,
                    annualTargetUzs = state.annualPlanTargetUzs,
                    totalPaidUzs = state.cumulativePaidAmount,
                    remainingDebtUzs = state.cumulativeRemainingDebt,
                    paymentType = state.selectedPaymentType.title
                )
            }
            _uiState.update {
                it.copy(
                    advisory = advisory,
                    isLoadingAdvisory = false
                )
            }
        }
    }

    fun viewReceipt(receipt: TransactionReceipt) {
        _uiState.update {
            it.copy(
                activeReceipt = receipt,
                showReceiptDialog = true
            )
        }
    }

    fun dismissReceiptDialog() {
        _uiState.update {
            it.copy(
                showReceiptDialog = false,
                activeReceipt = null
            )
        }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
