// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.haze.blur

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import dev.chrisbanes.haze.HazeEffectRuntimeDrawScope
import dev.chrisbanes.haze.HazeProgressive

internal actual fun RenderEffectBlurVisualEffectDelegate.drawProgressiveEffect(
  drawScope: DrawScope,
  progressive: HazeProgressive,
  contentLayer: GraphicsLayer,
  context: HazeEffectRuntimeDrawScope,
  inputScale: Float,
) = drawProgressiveEffectWithoutRuntimeShader(
  drawScope = drawScope,
  progressive = progressive,
  contentLayer = contentLayer,
  context = context,
  inputScale = inputScale,
)
