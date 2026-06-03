package com.example.brainbuilder.ui.views.screen

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.brainbuilder.ui.viewmodels.PaymentViewModel

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    paymentUrl: String,
    viewModel: PaymentViewModel,
    onPaymentComplete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Payment") })
        }
    ) { _ ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Midtrans redirect ke finish URL setelah payment
                            if (url?.contains("finish") == true ||
                                url?.contains("error") == true ||
                                url?.contains("unfinish") == true
                            ) {
                                val isSuccess = url.contains("finish") &&
                                        !url.contains("error")
                                viewModel.handlePaymentResult(isSuccess)
                                onPaymentComplete()
                            }
                        }
                    }
                    loadUrl(paymentUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}