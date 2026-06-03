package com.example.brainbuilder.ui.route

sealed class Route(val route: String) {
    object Subscription : Route("subscription")
    object Payment : Route("payment/{paymentUrl}") {
        fun createRoute(paymentUrl: String) = "payment/$paymentUrl"
    }
}