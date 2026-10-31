// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalHazeApi::class, InternalComposeUiApi::class)

package dev.chrisbanes.haze

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.OffsetEffect
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.webcanvas.blendRenderEffect
import androidx.compose.ui.webcanvas.colorFilterRenderEffect
import androidx.compose.ui.webcanvas.shaderRenderEffect

/** A Compose [RenderEffect], which the renderer of the web applies as Canvas 2D operations. */
@InternalHazeApi
public actual class PlatformRenderEffect internal constructor(
  internal val renderEffect: RenderEffect,
)

/** A Compose [ColorFilter]. */
@InternalHazeApi
public actual class PlatformColorFilter internal constructor(
  internal val colorFilter: ColorFilter,
)

/** Only glass crops its effects, and the web has no glass. */
private fun requireNoCrop(crop: Rect?) {
  require(crop == null) { "Render effects can't be cropped on the web" }
}

@InternalHazeApi
public actual fun createShaderRenderEffect(shader: Shader, crop: Rect?): PlatformRenderEffect {
  requireNoCrop(crop)
  return PlatformRenderEffect(shaderRenderEffect(shader))
}

@InternalHazeApi
public actual fun createBlendRenderEffect(
  blendMode: BlendMode,
  background: PlatformRenderEffect,
  foreground: PlatformRenderEffect,
  crop: Rect?,
): PlatformRenderEffect {
  requireNoCrop(crop)
  return PlatformRenderEffect(
    blendRenderEffect(blendMode, background.renderEffect, foreground.renderEffect),
  )
}

@InternalHazeApi
public actual fun createColorFilterRenderEffect(
  colorFilter: PlatformColorFilter,
  input: PlatformRenderEffect?,
  crop: Rect?,
): PlatformRenderEffect {
  requireNoCrop(crop)
  return PlatformRenderEffect(
    colorFilterRenderEffect(colorFilter.colorFilter, input?.renderEffect),
  )
}

@InternalHazeApi
public actual fun createBlurRenderEffect(
  radiusX: Float,
  radiusY: Float,
  tileMode: TileMode,
  input: PlatformRenderEffect?,
  crop: Rect?,
): PlatformRenderEffect? {
  requireNoCrop(crop)
  if (radiusX <= 0f && radiusY <= 0f) {
    return null
  }

  return PlatformRenderEffect(BlurEffect(input?.renderEffect, radiusX, radiusY, tileMode))
}

@InternalHazeApi
public actual fun createOffsetRenderEffect(
  offsetX: Float,
  offsetY: Float,
  input: PlatformRenderEffect?,
  crop: Rect?,
): PlatformRenderEffect {
  requireNoCrop(crop)
  return PlatformRenderEffect(OffsetEffect(input?.renderEffect, Offset(offsetX, offsetY)))
}

/** Only progressive blur through a runtime shader chains effects, and the web has no runtime shaders. */
@InternalHazeApi
public actual fun PlatformRenderEffect.then(other: PlatformRenderEffect): PlatformRenderEffect =
  throw UnsupportedOperationException("Render effects can't be chained on the web")

@InternalHazeApi
public actual fun PlatformRenderEffect.asComposeRenderEffect(): RenderEffect = renderEffect
