package com.example.brainbuilder.ui.route

sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Subscription : Route("subscription")
    object Payment : Route("payment/{paymentUrl}") {
        fun createRoute(paymentUrl: String) = "payment/$paymentUrl"
    }
    object AdminDashboard : Route("admin_dashboard")
    object CourseList : Route("course_list")
    object Lesson : Route("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}