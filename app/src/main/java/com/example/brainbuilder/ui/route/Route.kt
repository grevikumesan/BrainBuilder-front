package com.example.brainbuilder.ui.route

sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Subscription : Route("subscription")
    object Payment : Route("payment/{paymentUrl}") {
        fun createRoute(paymentUrl: String) = "payment/$paymentUrl"
    }
    object CreateCourse : Route("create_course")
    object AdminDashboard : Route("admin_dashboard")
    object StudentHome : Route("student_home")
    object Progress : Route("progress")
    object Lesson : Route("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Quiz : Route("quiz/{lessonId}") {
        fun createRoute(lessonId: String) = "quiz/$lessonId"
    }

    object CourseDetail : Route("course_detail/{courseId}") {
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
}