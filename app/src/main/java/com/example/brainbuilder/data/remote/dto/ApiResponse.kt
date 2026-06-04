package com.example.brainbuilder.data.remote.dto

/**
 * Generic envelope returned by every Brain Builder edge function:
 *   success: { "success": true,  "data": <payload> }
 *   error:   { "success": false, "error": "<message>" }
 * Callers unwrap [data] on success and read [error] otherwise.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)
