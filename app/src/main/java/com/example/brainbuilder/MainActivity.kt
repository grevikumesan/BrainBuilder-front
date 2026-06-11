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
import com.example.brainbuilder.ui.viewmodels.AdminViewModel
import com.example.brainbuilder.ui.viewmodels.AdminViewModelFactory
import com.example.brainbuilder.ui.viewmodels.AuthViewModel
import com.example.brainbuilder.ui.viewmodels.AuthViewModelFactory
import com.example.brainbuilder.ui.viewmodels.ContentEditorViewModel
import com.example.brainbuilder.ui.viewmodels.ContentEditorViewModelFactory
import com.example.brainbuilder.ui.viewmodels.CourseDetailViewModel
import com.example.brainbuilder.ui.viewmodels.CourseDetailViewModelFactory
import com.example.brainbuilder.ui.viewmodels.CourseViewModel
import com.example.brainbuilder.ui.viewmodels.CourseViewModelFactory
import com.example.brainbuilder.ui.viewmodels.PaymentViewModel
import com.example.brainbuilder.ui.viewmodels.PaymentViewModelFactory
import com.example.brainbuilder.ui.viewmodels.ProgressViewModel
import com.example.brainbuilder.ui.viewmodels.ProgressViewModelFactory
import com.example.brainbuilder.ui.viewmodels.QuizViewModel
import com.example.brainbuilder.ui.viewmodels.QuizViewModelFactory
import com.example.brainbuilder.ui.views.screen.AdminDashboardScreen
import com.example.brainbuilder.ui.views.screen.ContentEditorScreen
import com.example.brainbuilder.ui.views.screen.CourseDetailScreen
import com.example.brainbuilder.ui.views.screen.CourseListScreen
import com.example.brainbuilder.ui.views.screen.LessonScreen
import com.example.brainbuilder.ui.views.screen.LoginScreen
import com.example.brainbuilder.ui.views.screen.PaymentScreen
import com.example.brainbuilder.ui.views.screen.ProgressScreen
import com.example.brainbuilder.ui.views.screen.QuizScreen
import com.example.brainbuilder.ui.views.screen.RegisterScreen
import com.example.brainbuilder.ui.views.screen.StudentShell
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

                val courseDetailViewModel = viewModel<CourseDetailViewModel>(
                    factory = CourseDetailViewModelFactory(appContainer.courseDetailRepository)
                )

                val contentEditorViewModel = viewModel<ContentEditorViewModel>(
                    factory = ContentEditorViewModelFactory(appContainer.contentEditorRepository)
                )

                val adminViewModel = viewModel<AdminViewModel>(
                    factory = AdminViewModelFactory(appContainer.adminRepository)
                )

                val progressViewModel = viewModel<ProgressViewModel>(
                    factory = ProgressViewModelFactory(appContainer.progressRepository)
                )

                // Clears the JWT and returns to login with the whole back stack wiped,
                // so a logged-out user (or a role switch) can't navigate back into the app.
                val onLogout: () -> Unit = {
                    authViewModel.logout {
                        navController.navigate(Route.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }

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
                            onLoginSuccess = { role ->
                                val destination = when (role) {
                                    "TEACHER" -> Route.CreateCourse.route
                                    "ADMIN" -> Route.AdminDashboard.route
                                    else -> Route.StudentHome.route
                                }
                                navController.navigate(destination) {
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

                    composable(Route.CreateCourse.route) {
                        ContentEditorScreen(
                            viewModel = contentEditorViewModel,
                            onLogout = onLogout
                        )
                    }

                    composable(Route.AdminDashboard.route) {
                        AdminDashboardScreen(
                            viewModel = adminViewModel,
                            onLogout = onLogout
                        )
                    }

                    composable(Route.StudentHome.route) {
                        StudentShell(
                            courseViewModel = courseViewModel,
                            paymentViewModel = paymentViewModel,
                            onCourseSelected = { courseId ->
                                navController.navigate(Route.CourseDetail.createRoute(courseId))
                            },
                            onPayNow = { paymentUrl ->
                                val encoded = java.net.URLEncoder.encode(paymentUrl, "UTF-8")
                                navController.navigate(Route.Payment.createRoute(encoded))
                            },
                            onOpenProgress = { navController.navigate(Route.Progress.route) },
                            onLogout = onLogout
                        )
                    }

                    composable(Route.CourseList.route) {
                        CourseListScreen(
                            viewModel = courseViewModel,
                            onCourseSelected = { courseId ->
                                navController.navigate(Route.CourseDetail.createRoute(courseId))
                            },
                            onOpenProgress = {
                                navController.navigate(Route.Progress.route)
                            },
                            onLogout = onLogout
                        )
                    }

                    composable(Route.Progress.route) {
                        ProgressScreen(
                            viewModel = progressViewModel,
                            onBack = { navController.popBackStack() }
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
                            },
                            onBack = { navController.popBackStack() }
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

                    composable(
                        route = Route.CourseDetail.route,
                        arguments = listOf(
                            navArgument("courseId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        CourseDetailScreen(
                            courseId = courseId,
                            viewModel = courseDetailViewModel,
                            onBack = { navController.popBackStack() },
                            onLessonSelected = { lessonId ->
                                navController.navigate(Route.Lesson.createRoute(lessonId))
                            }
                        )
                    }

                    composable(Route.Subscription.route) {
                        SubscriptionScreen(
                            viewModel = paymentViewModel,
                            onPayNow = { paymentUrl ->
                                val encoded = java.net.URLEncoder.encode(paymentUrl, "UTF-8")
                                navController.navigate(Route.Payment.createRoute(encoded))
                            },
                            onBack = { navController.popBackStack() }
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
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
