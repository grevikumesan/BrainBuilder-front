package com.example.brainbuilder.data.remote.service

import com.example.brainbuilder.data.remote.dto.ExplanationItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * UC-04 Show Explanation.
 * Explanations have no edge-function endpoint; they are read directly from
 * Supabase PostgREST. RLS only returns rows once the student has submitted the
 * quiz (and the course is approved / premium is satisfied). The apikey + student
 * JWT headers are attached by the dedicated REST OkHttp client in AppContainer.
 */
interface ExplanationService {
    @GET("explanations")
    suspend fun getExplanations(
        // PostgREST filter, e.g. "in.(<id1>,<id2>)"
        @Query("question_id") questionIdFilter: String,
        @Query("select") select: String = "question_id,steps"
    ): Response<List<ExplanationItem>>
}
