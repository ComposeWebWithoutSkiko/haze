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

internal actual fun BlurVisualEffect.invalidateRenderEffectOnInputChange() {
  if (Build.VERSION.SDK_INT == 31) {
    // API 31 can retain blurred pixels after a nested source layer changes, even when the
    // input is re-recorded. A fresh RenderEffect refreshes the output without replacing the
    // capture layer, which another effect may be sampling.
    clearRenderEffectCache()
  }
}

@RequiresApi(31)
internal actual fun RenderEffectBlurVisualEffectDelegate.drawProgressiveEffect(
  drawScope: DrawScope,
  progressive: RootHazeProgressive,
  contentLayer: GraphicsLayer,
  context: HazeEffectRuntimeDrawScope,
  inputScale: Float,
) {
  if (Build.VERSION.SDK_INT >= 33) {
    trimBandSlots(0)
    with(drawScope) {
      effectSlot.apply(
        contentLayer,
        blurVisualEffect.getOrCreateRenderEffect(
          context = context,
          inputScale = inputScale,
          progressive = progressive,
          slot = effectSlot,
        ),
      )
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
