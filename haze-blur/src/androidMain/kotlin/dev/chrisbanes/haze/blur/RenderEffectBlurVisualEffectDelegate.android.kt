// Copyright 2025, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalHazeApi::class, ExperimentalHazeApi::class)

package dev.chrisbanes.haze.blur

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectRuntimeDrawScope
import dev.chrisbanes.haze.HazeProgressive as RootHazeProgressive
import dev.chrisbanes.haze.InternalHazeApi
import dev.chrisbanes.haze.asComposeRenderEffect

@OptIn(InternalHazeApi::class)
private const val USE_RUNTIME_SHADER = true

@RequiresApi(31)
internal actual fun RenderEffectBlurVisualEffectDelegate.drawProgressiveEffect(
  drawScope: DrawScope,
  progressive: RootHazeProgressive,
  contentLayer: GraphicsLayer,
  context: HazeEffectRuntimeDrawScope,
  inputScale: Float,
) {
  if (USE_RUNTIME_SHADER && Build.VERSION.SDK_INT >= 33) {
    with(drawScope) {
      contentLayer.renderEffect = blurVisualEffect
        .getOrCreateRenderEffect(
          context = context,
          inputScale = inputScale,
          progressive = progressive,
        )
        .asComposeRenderEffect()
      contentLayer.alpha = blurVisualEffect.alpha

      // Finally draw the layer
      drawLayer(contentLayer)
    }
  } else {
    drawProgressiveEffectWithoutRuntimeShader(
      drawScope = drawScope,
      progressive = progressive,
      contentLayer = contentLayer,
      context = context,
      inputScale = inputScale,
    )
  }
}
