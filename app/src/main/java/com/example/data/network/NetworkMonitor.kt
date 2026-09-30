package com.example.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isDeviceConnected = MutableStateFlow(checkInitialConnectivity())
    private val _isManualOfflineMode = MutableStateFlow(false)

    private val _effectiveOnlineState = MutableStateFlow(calculateEffectiveOnline())
    val isOnline: StateFlow<Boolean> = _effectiveOnlineState.asStateFlow()
    val isManualOffline: StateFlow<Boolean> = _isManualOfflineMode.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isDeviceConnected.value = true
            updateEffectiveOnline()
        }

        override fun onLost(network: Network) {
            _isDeviceConnected.value = false
            updateEffectiveOnline()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            _isDeviceConnected.value = hasInternet
            updateEffectiveOnline()
        }
    }

    init {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (_: Exception) {
            // Fallback for restricted environments
        }
    }

    private fun checkInitialConnectivity(): Boolean {
        val cm = connectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun updateEffectiveOnline() {
        _effectiveOnlineState.value = calculateEffectiveOnline()
    }

    private fun calculateEffectiveOnline(): Boolean {
        if (_isManualOfflineMode.value) return false
        return _isDeviceConnected.value
    }

    fun toggleManualOfflineMode(enable: Boolean? = null) {
        _isManualOfflineMode.value = enable ?: !_isManualOfflineMode.value
        updateEffectiveOnline()
    }
}
