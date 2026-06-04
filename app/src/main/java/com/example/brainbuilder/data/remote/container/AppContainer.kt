package com.example.brainbuilder.data.remote.container

import android.content.Context
import com.example.brainbuilder.data.local.DataStore
import com.example.brainbuilder.data.remote.repository.AuthRepository
import com.example.brainbuilder.data.remote.repository.CourseRepository
import com.example.brainbuilder.data.remote.repository.PaymentRepository
import com.example.brainbuilder.data.remote.service.AuthService
import com.example.brainbuilder.data.remote.service.CourseService
import com.example.brainbuilder.data.remote.service.PaymentService
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

    private val paymentService: PaymentService = retrofit.create(PaymentService::class.java)
    private val authService: AuthService = retrofit.create(AuthService::class.java)
    private val courseService: CourseService = retrofit.create(CourseService::class.java)

    val paymentRepository: PaymentRepository = PaymentRepository(paymentService)
    val authRepository: AuthRepository = AuthRepository(authService)
    val courseRepository: CourseRepository = CourseRepository(courseService)
}