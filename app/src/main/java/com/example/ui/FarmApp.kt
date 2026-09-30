package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdvisoryCard
import com.example.ui.components.AuthScreen
import com.example.ui.components.DashboardCard
import com.example.ui.components.FarmBankCard
import com.example.ui.components.FarmerSelectorDialog
import com.example.ui.components.PaymentFormCard
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.TelegramSettingsDialog
import com.example.ui.components.TransactionHistorySection
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmApp(viewModel: FarmViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val allFarmers by viewModel.allFarmers.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showFarmerSelector by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // 1. If not authenticated, display AuthScreen
    if (uiState.currentUser == null) {
        AuthScreen(
            loginUsernameInput = uiState.loginUsernameInput,
            loginPasswordInput = uiState.loginPasswordInput,
            loginError = uiState.loginError,
            isAuthenticating = uiState.isAuthenticating,
            onLoginUsernameChanged = viewModel::onLoginUsernameChanged,
            onLoginPasswordChanged = viewModel::onLoginPasswordChanged,
            onQuickSelectAccount = viewModel::quickSelectLoginAccount,
            onLoginSubmit = viewModel::login
        )
        return
    }

    // 2. User is logged in: filter payments for dehqon vs rahbar
    val currentUser = uiState.currentUser!!
    val isRahbar = uiState.isRahbar

    // Dehqon only sees their own receipts; Rahbar can see all or selected
    val visiblePayments = if (isRahbar) {
        allPayments
    } else {
        allPayments.filter { it.dehqonId == currentUser.dehqonId }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("farm_app_root"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = FarmGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ittifoq Fermer Xo'jaligi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarmGreenDark
                            )
                        }
                        Text(
                            text = if (isRahbar) "👑 Rahbar: Abdumalikov Abdurahmon" else "🌾 Kabinet: ${currentUser.name} (${currentUser.dehqonId})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Offline / Online Mode Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (uiState.isOnline) FarmGreenContainer else Color(0xFFFFEBEE),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.toggleOfflineMode() }
                            .testTag("connection_status_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (uiState.isOnline) Icons.Default.Wifi else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (uiState.isOnline) FarmGreenPrimary else Color(0xFFC62828),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.isOnline) "Onlayn" else "Oflayn",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isOnline) FarmGreenPrimary else Color(0xFFC62828)
                            )
                        }
                    }

                    // Rahbar specific actions: Telegram settings & Brigade switcher
                    if (isRahbar) {
                        IconButton(
                            onClick = viewModel::openTelegramSettings,
                            modifier = Modifier.testTag("telegram_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Telegram Bot",
                                tint = Color(0xFF0288D1)
                            )
                        }

                        IconButton(
                            onClick = { showFarmerSelector = true },
                            modifier = Modifier.testTag("brigade_farmers_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Fermerlar",
                                tint = FarmGreenPrimary
                            )
                        }
                    }

                    // Logout Button
                    IconButton(
                        onClick = viewModel::logout,
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Chiqish",
                            tint = Color(0xFFC62828)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Kabinet") },
                    label = { Text(if (isRahbar) "Boshqaruv" else "Kabinetim") },
                    colors = navigationBarColors()
                )
                NavigationBarItem(
                    selected = uiState.currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.advisory != null) {
                                    Badge(containerColor = HarvestGold) {
                                        Text("AI", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Agronom")
                        }
                    },
                    label = { Text("AI Agronom") },
                    colors = navigationBarColors()
                )
                NavigationBarItem(
                    selected = uiState.currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (visiblePayments.isNotEmpty()) {
                                    Badge(containerColor = FarmGreenPrimary) {
                                        Text("${visiblePayments.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Cheklar")
                        }
                    },
                    label = { Text(if (isRahbar) "Barcha Cheklar" else "Cheklarim") },
                    colors = navigationBarColors()
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState.currentTab) {
                0 -> {
                    // TAB 0: Dashboard Card + Bank Card (for Dehqon) + Payment Form
                    item {
                        DashboardCard(
                            farmerName = uiState.currentFarmerName,
                            dehqonId = uiState.currentDehqonId,
                            landSizeHa = uiState.landSizeHectares,
                            totalPaidUzs = uiState.cumulativePaidAmount,
                            annualTargetUzs = uiState.annualPlanTargetUzs,
                            remainingDebtUzs = uiState.cumulativeRemainingDebt,
                            completionPercentage = uiState.cumulativePercentage,
                            activePaymentType = uiState.selectedPaymentType,
                            isOnline = uiState.isOnline
                        )
                    }

                    // Dehqon: Show official bank card with 1-click copy feature
                    if (!isRahbar) {
                        item {
                            FarmBankCard(
                                onCopyToast = viewModel::showToast
                            )
                        }
                    }

                    item {
                        PaymentFormCard(
                            farmerName = uiState.currentFarmerName,
                            dehqonId = uiState.currentDehqonId,
                            landSizeInput = uiState.landSizeInput,
                            paymentAmountInput = uiState.paymentAmountInput,
                            annualPlanInput = uiState.annualPlanInput,
                            selectedPaymentType = uiState.selectedPaymentType,
                            notesInput = uiState.notesInput,
                            selectedExpenseCategory = uiState.selectedExpenseCategory,
                            isRahbar = isRahbar,
                            calculatedRemainingBalance = uiState.calculatedRemainingBalance,
                            calculatedCompletionPercentage = uiState.calculatedCompletionPercentage,
                            isProcessing = uiState.isProcessingPayment,
                            onFarmerNameOrIdChanged = viewModel::onFarmerNameInputChanged,
                            onLandSizeChanged = viewModel::onLandSizeChanged,
                            onPaymentAmountChanged = viewModel::onPaymentAmountChanged,
                            onAnnualPlanTargetChanged = viewModel::onAnnualPlanTargetChanged,
                            onPaymentTypeChanged = viewModel::onPaymentTypeChanged,
                            onExpenseCategoryChanged = viewModel::onExpenseCategoryChanged,
                            onNotesChanged = viewModel::onNotesChanged,
                            onPresetAmountSelected = viewModel::onSelectPresetAmount,
                            onOpenFarmerSelector = { showFarmerSelector = true },
                            onSubmitPayment = viewModel::submitPayment
                        )
                    }

                    // Compact preview of recent receipts
                    item {
                        TransactionHistorySection(
                            payments = visiblePayments.take(3),
                            selectedFilter = uiState.historyFilter,
                            onFilterChange = viewModel::setHistoryFilter,
                            onReceiptClick = viewModel::viewReceipt
                        )
                    }
                }

                1 -> {
                    // TAB 1: AI Agronom Advisory Card with Offline support
                    item {
                        AdvisoryCard(
                            advisory = uiState.advisory,
                            isLoading = uiState.isLoadingAdvisory,
                            isOnline = uiState.isOnline,
                            landSizeHa = uiState.landSizeHectares,
                            onRefresh = viewModel::refreshAdvisory,
                            onToggleOfflineMode = viewModel::toggleOfflineMode
                        )
                    }
                }

                2 -> {
                    // TAB 2: Full Transaction History
                    item {
                        TransactionHistorySection(
                            payments = visiblePayments,
                            selectedFilter = uiState.historyFilter,
                            onFilterChange = viewModel::setHistoryFilter,
                            onReceiptClick = viewModel::viewReceipt
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Dialog: Official Transaction Receipt
    if (uiState.showReceiptDialog && uiState.activeReceipt != null) {
        ReceiptDialog(
            receipt = uiState.activeReceipt!!,
            onDismiss = viewModel::dismissReceiptDialog
        )
    }

    // Modal Dialog: Telegram Bot Settings (Rahbar)
    if (uiState.showTelegramSettingsDialog) {
        TelegramSettingsDialog(
            botTokenInput = uiState.telegramBotTokenInput,
            chatIdInput = uiState.telegramChatIdInput,
            onTokenChange = viewModel::onTgBotTokenChanged,
            onChatIdChange = viewModel::onTgChatIdChanged,
            onSave = viewModel::saveTelegramSettings,
            onDismiss = viewModel::closeTelegramSettings
        )
    }

    // Modal Dialog: Farmer Switcher / Registration (Rahbar)
    if (showFarmerSelector) {
        FarmerSelectorDialog(
            farmers = allFarmers,
            currentDehqonId = uiState.currentDehqonId,
            onSelectFarmer = { farmer ->
                viewModel.onFarmerSelected(farmer)
                showFarmerSelector = false
            },
            onAddNewFarmer = { name, id, land, target ->
                // Rahbar can add new dehqon directly
                viewModel.onFarmerSelected(
                    com.example.data.local.FarmerEntity(
                        dehqonId = id.ifBlank { "IFX-2026-${(100..999).random()}" },
                        name = name.ifBlank { "Yangi Dehqon" },
                        landSizeHectares = land,
                        annualPlanTargetUzs = target
                    )
                )
                showFarmerSelector = false
            },
            onDismiss = { showFarmerSelector = false }
        )
    }
}

@Composable
private fun navigationBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = FarmGreenPrimary,
    selectedTextColor = FarmGreenPrimary,
    indicatorColor = FarmGreenContainer,
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)
