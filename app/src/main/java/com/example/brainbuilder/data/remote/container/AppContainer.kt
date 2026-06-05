package com.example.brainbuilder.data.remote.container

import android.content.Context
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.repository.AdminRepository
import com.example.brainbuilder.data.remote.repository.AuthRepository
import com.example.brainbuilder.data.remote.repository.ContentEditorRepository
import com.example.brainbuilder.data.remote.repository.CourseDetailRepository
import com.example.brainbuilder.data.remote.repository.CourseRepository
import com.example.brainbuilder.data.remote.repository.PaymentRepository
import com.example.brainbuilder.data.remote.repository.ProgressRepository
import com.example.brainbuilder.data.remote.repository.QuizRepository
import com.example.brainbuilder.data.remote.service.AdminService
import com.example.brainbuilder.data.remote.service.AuthService
import com.example.brainbuilder.data.remote.service.ContentEditorService
import com.example.brainbuilder.data.remote.service.CourseDetailService
import com.example.brainbuilder.data.remote.service.CourseService
import com.example.brainbuilder.data.remote.service.ExplanationService
import com.example.brainbuilder.data.remote.service.PaymentService
import com.example.brainbuilder.data.remote.service.ProgressService
import com.example.brainbuilder.data.remote.service.QuizService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    val dataStore: DataStore = DataStore(context)

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private val isDebug = (context.applicationInfo.flags and
            android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = runBlocking { dataStore.getToken() }
            val request = if (token.isNotEmpty()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://boofqcpycuoujiscahti.supabase.co/functions/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Public "anon" key — safe to ship in the client. Get it from the Supabase
    // dashboard: Project Settings -> API -> Project API keys -> "anon public".
    // Required as the `apikey` header for UC-04 PostgREST explanation reads.
    private val supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJvb2ZxY3B5Y3VvdWppc2NhaHRpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAwMzM2ODMsImV4cCI6MjA5NTYwOTY4M30.symDon--YxM38EZ5B0Na5e3tLlGv-qJp_Ao2UGHDMUY"

    // PostgREST (/rest/v1) requires the anon apikey in addition to the user JWT
    // that drives Row Level Security. Used only by UC-04 explanation reads.
    private val restOkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = runBlocking { dataStore.getToken() }
            val builder = chain.request().newBuilder()
                .addHeader("apikey", supabaseAnonKey)
            if (token.isNotEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }
        .addInterceptor(loggingInterceptor)
        .build()

    private val restRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://boofqcpycuoujiscahti.supabase.co/rest/v1/")
        .client(restOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val paymentService: PaymentService = retrofit.create(PaymentService::class.java)
    private val authService: AuthService = retrofit.create(AuthService::class.java)
    private val adminService: AdminService = retrofit.create(AdminService::class.java)
    private val courseService: CourseService = retrofit.create(CourseService::class.java)
    private val quizService = retrofit.create(QuizService::class.java)
    private val courseDetailService: CourseDetailService = retrofit.create(CourseDetailService::class.java)
    private val contentEditorService: ContentEditorService = retrofit.create(ContentEditorService::class.java)
    private val progressService: ProgressService = retrofit.create(ProgressService::class.java)
    private val explanationService: ExplanationService = restRetrofit.create(ExplanationService::class.java)


    val paymentRepository: PaymentRepository = PaymentRepository(paymentService)
    val progressRepository: ProgressRepository = ProgressRepository(progressService)
    val authRepository: AuthRepository = AuthRepository(authService)
    val adminRepository: AdminRepository = AdminRepository(adminService)
    val courseRepository: CourseRepository = CourseRepository(courseService)
    val quizRepository = QuizRepository(quizService, courseService, explanationService)
    val courseDetailRepository: CourseDetailRepository = CourseDetailRepository(courseDetailService)
    val contentEditorRepository: ContentEditorRepository = ContentEditorRepository(contentEditorService)
}
