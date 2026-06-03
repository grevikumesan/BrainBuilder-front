package com.example.brainbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.brainbuilder.data.remote.container.AppContainer
import com.example.brainbuilder.ui.route.Route
import com.example.brainbuilder.ui.theme.BrainBuilderTheme
import com.example.brainbuilder.ui.viewmodels.PaymentViewModelFactory
import com.example.brainbuilder.ui.views.screen.PaymentScreen
import com.example.brainbuilder.ui.views.screen.SubscriptionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer(applicationContext)

        setContent {
            BrainBuilderTheme {
                val navController = rememberNavController()
                val paymentViewModel = viewModel<com.example.brainbuilder.ui.viewmodels.PaymentViewModel>(
                    factory = PaymentViewModelFactory(appContainer.paymentRepository)
                )

                NavHost(
                    navController = navController,
                    startDestination = Route.Subscription.route
                ) {
                    composable(Route.Subscription.route) {
                        SubscriptionScreen(
                            viewModel = paymentViewModel,
                            onPayNow = { paymentUrl ->
                                val encoded = java.net.URLEncoder.encode(paymentUrl, "UTF-8")
                                navController.navigate(Route.Payment.createRoute(encoded))
                            }
                        )
                    }

                    composable(
                        route = Route.Payment.route,
                        arguments = listOf(
                            navArgument("paymentUrl") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val encoded = backStackEntry.arguments?.getString("paymentUrl") ?: ""
                        val decoded = java.net.URLDecoder.decode(encoded, "UTF-8")
                        PaymentScreen(
                            paymentUrl = decoded,
                            viewModel = paymentViewModel,
                            onPaymentComplete = {
                                navController.popBackStack(Route.Subscription.route, false)
                            }
                        )
                    }
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    BrainBuilderTheme {
//        Greeting("Android")
//    }
//}