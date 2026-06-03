package com.example.brainbuilder.ui.route

sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Subscription : Route("subscription")
    object Payment : Route("payment/{paymentUrl}") {
        fun createRoute(paymentUrl: String) = "payment/$paymentUrl"
    }
}