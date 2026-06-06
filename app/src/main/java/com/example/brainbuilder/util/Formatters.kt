package com.example.brainbuilder.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Shared formatting helpers. Pure functions, no Android/Compose dependency, so they
 * can be reused from any screen and unit-tested in isolation.
 */

private val rupiahFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

fun formatRupiah(amount: Number): String = rupiahFormatter.format(amount)
