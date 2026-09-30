package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.data.local.FarmDatabase
import com.example.data.network.GeminiFarmService
import com.example.data.network.NetworkMonitor
import com.example.data.network.TelegramBotService
import com.example.data.repository.FarmRepository
import com.example.ui.FarmApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FarmViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FarmDatabase.getDatabase(applicationContext, lifecycleScope)
        val geminiService = GeminiFarmService()
        val networkMonitor = NetworkMonitor(applicationContext)
        val telegramBotService = TelegramBotService(applicationContext)
        val repository = FarmRepository(database.farmDao(), geminiService, telegramBotService)

        viewModel = FarmViewModel(repository, networkMonitor)

        setContent {
            MyApplicationTheme {
                FarmApp(viewModel = viewModel)
            }
        }
    }
}
