package com.example.appnovation_test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appnovation_test.compose.BanksListScreenComposable
import com.example.appnovation_test.model.BankInfo
import com.example.appnovation_test.ui.theme.Appnovation_testTheme
import com.example.appnovation_test.viewmodels.BanksViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class BankListActivity : ComponentActivity() {
    private val banksViewModel: BanksViewModel by viewModels<BanksViewModel.Impl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val event by banksViewModel.event.collectAsState()
            
            // Observe events and handle navigation
            LaunchedEffect(event) {
                when (event) {
                    is BanksViewModel.Event.NavigateToPhoneActivity -> {
                        val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = (event as BanksViewModel.Event.NavigateToPhoneActivity).phoneNumber.toUri()
                        }
                        context.startActivity(phoneIntent)
                    }
                    is BanksViewModel.Event.NavigateToDeepLink -> {
                        // Open URL/deep link in browser or appropriate app
                        val deepLinkIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = (event as BanksViewModel.Event.NavigateToDeepLink).url.toUri()
                        }
                        context.startActivity(deepLinkIntent)
                    }
                    else -> {}
                }

                // Clear the event to prevent re-triggering
                banksViewModel.clearEvent()
            }
            
            Appnovation_testTheme {
                BanksListScreenComposable(banksViewModel = banksViewModel)
            }
        }
    }
}