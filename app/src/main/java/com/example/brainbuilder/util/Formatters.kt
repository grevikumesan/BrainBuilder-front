package com.example.brainbuilder.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Shared formatting/display helpers. Pure functions with no Android/Compose
 * dependency, so they can be reused from any screen and unit-tested in isolation.
 */

private val rupiahFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

fun formatRupiah(amount: Number): String = rupiahFormatter.format(amount)

/** Maps a Kemendikdasmen subject to a friendly icon used on cards/badges. */
fun subjectEmoji(subject: String): String = when (subject.uppercase()) {
    "MATHEMATICS" -> "🧮"
    "PHYSICS" -> "🔬"
    "CHEMISTRY" -> "🧪"
    else -> "📘"
}

/** Turns an ISO-8601 timestamp (e.g. 2026-06-05T10:15:30Z) into "05 Jun 2026". */
fun formatDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(iso.take(19)) ?: return iso.substringBefore("T")
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        iso.substringBefore("T")
    }
}
