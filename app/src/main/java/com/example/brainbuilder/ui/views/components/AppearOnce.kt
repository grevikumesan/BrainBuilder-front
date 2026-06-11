package com.example.brainbuilder.ui.views.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * One-shot fade + rise entrance the first time the content appears, staggered by
 * [index] so a list reveals top-to-bottom. The stagger is capped so items deep in a
 * long list don't wait too long before showing.
 */
@Composable
fun AppearOnce(
    index: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(minOf(index, 6) * 50L)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "appearAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 350),
        label = "appearOffset"
    )
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .alpha(alpha)
    ) {
        content()
    }
}
