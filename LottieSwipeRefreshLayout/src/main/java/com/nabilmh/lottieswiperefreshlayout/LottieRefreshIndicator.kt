package com.photobook.android.widgets.compose

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.photobook.android.R

/**
 * A custom indicator which displays a LottieSwipeRefreshIndicator
 */
@Composable
fun LottieRefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    animation: Int = R.raw.photobook_loader,
    backgroundColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    refreshingOffset: Dp = 16.dp,
    indicatorSize: Dp = 40.dp,
    elevation: Dp = 6.dp,
) {
    // Otherwise we display a determinate progress indicator with the current swipe progress
    val trigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
    val progress = (state.indicatorOffset / trigger).coerceIn(0f, 1f)
    val indicatorHeight = with(LocalDensity.current) { indicatorSize.roundToPx() }
    val refreshingOffsetPx = with(LocalDensity.current) { refreshingOffset.toPx() }

    var offset by remember { mutableStateOf(0f) }

    if (state.isSwipeInProgress) {
        offset = if (state.indicatorOffset > trigger * 2) trigger * 2 else state.indicatorOffset
    }

    LaunchedEffect(state.isSwipeInProgress, state.isRefreshing) {
        // If there's no swipe currently in progress, animate to the correct resting position
        if (!state.isSwipeInProgress) {
            animate(
                initialValue = offset,
                targetValue = when {
                    state.isRefreshing -> indicatorHeight + refreshingOffsetPx
                    else -> 0f
                }
            ) { value, _ ->
                offset = value
            }
        }
    }

    Surface(
        modifier = Modifier
            .size(size = indicatorSize)
            .graphicsLayer {
                // Translate the indicator according to the slingshot
                translationY = offset - indicatorHeight
            },
        shape = shape,
        color = backgroundColor,
        elevation = elevation
    ) {
        AndroidView(
            { context ->
                LottieAnimationView(context).apply {
                    setAnimation(animation)
                    repeatCount = LottieDrawable.INFINITE
                }
            },
            update = {
                if (state.isRefreshing) {
                    // If we're refreshing, show an indeterminate progress indicator
                    it.resumeAnimation()
                } else {
                    it.progress = progress
                    it.cancelAnimation()
                }
            },
        )
    }
}
