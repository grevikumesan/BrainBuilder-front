package com.example.brainbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.brainbuilder.data.remote.container.AppContainer
import com.example.brainbuilder.ui.route.Route
import com.example.brainbuilder.ui.theme.BrainBuilderTheme
import com.example.brainbuilder.ui.viewmodels.AuthViewModelFactory
import com.example.brainbuilder.ui.viewmodels.AuthViewModel
import com.example.brainbuilder.ui.viewmodels.CourseViewModel
import com.example.brainbuilder.ui.viewmodels.CourseViewModelFactory
import com.example.brainbuilder.ui.viewmodels.PaymentViewModel
import com.example.brainbuilder.ui.viewmodels.PaymentViewModelFactory
import com.example.brainbuilder.ui.viewmodels.QuizViewModel
import com.example.brainbuilder.ui.viewmodels.QuizViewModelFactory
import com.example.brainbuilder.ui.views.screen.CourseListScreen
import com.example.brainbuilder.ui.views.screen.LessonScreen
import com.example.brainbuilder.ui.views.screen.LoginScreen
import com.example.brainbuilder.ui.views.screen.PaymentScreen
import com.example.brainbuilder.ui.views.screen.QuizScreen
import com.example.brainbuilder.ui.views.screen.RegisterScreen
import com.example.brainbuilder.ui.views.screen.SubscriptionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer(applicationContext)

        setContent {
            BrainBuilderTheme {
                val navController = rememberNavController()

                val authViewModel = viewModel<AuthViewModel>(
                    factory = AuthViewModelFactory(appContainer.authRepository, appContainer.dataStore)
                )

                val paymentViewModel = viewModel<PaymentViewModel>(
                    factory = PaymentViewModelFactory(appContainer.paymentRepository)
                )

                val courseViewModel = viewModel<CourseViewModel>(
                    factory = CourseViewModelFactory(appContainer.courseRepository)
                )

                val quizViewModel = viewModel<QuizViewModel>(
                    factory = QuizViewModelFactory(appContainer.quizRepository)
                )

                NavHost(
                    navController = navController,
                    startDestination = Route.Login.route
                ) {
                    composable(Route.Login.route) {
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToRegister = {
                                navController.navigate(Route.Register.route)
                            },
                            onLoginSuccess = { _ ->
                                navController.navigate(Route.CourseList.route) {
                                    popUpTo(Route.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Route.Register.route) {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Route.CourseList.route) {
                        CourseListScreen(
                            viewModel = courseViewModel,
                            onCourseSelected = { courseId ->
                                navController.navigate(Route.Lesson.createRoute(courseId))
                            }
                        )
                    }

                    composable(
                        route = Route.Lesson.route,
                        arguments = listOf(
                            navArgument("lessonId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                        LessonScreen(
                            lessonId = lessonId,
                            viewModel = courseViewModel,
                            onStartQuiz = { lessonId ->
                                navController.navigate(Route.Quiz.createRoute(lessonId))
                            },
                            onSubscribeRequired = {
                                navController.navigate(Route.Subscription.route)
                            }
                        )
                    }

                    composable(
                        route = Route.Quiz.route,
                        arguments = listOf(
                            navArgument("lessonId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                        QuizScreen(
                            lessonId = lessonId,
                            viewModel = quizViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

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