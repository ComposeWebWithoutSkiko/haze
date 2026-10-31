// Copyright 2023, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalHazeApi::class)

package dev.chrisbanes.haze.blur

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import dev.chrisbanes.haze.InternalHazeApi
import dev.chrisbanes.haze.PlatformContext
import dev.chrisbanes.haze.PlatformRenderEffect
import dev.chrisbanes.haze.createBlendRenderEffect
import dev.chrisbanes.haze.createFractalNoiseShader
import dev.chrisbanes.haze.createShaderRenderEffect

private val NOISE_SHADER by lazy(LazyThreadSafetyMode.NONE) {
  createFractalNoiseShader(
    baseFrequencyX = 0.45f,
    baseFrequencyY = 0.45f,
    numOctaves = 4,
    seed = 2.0f,
  )
}

internal actual fun createCombinedNoiseTintRenderEffectOrNull(
  context: PlatformContext,
  input: PlatformRenderEffect,
  noiseFactor: Float,
  tintColor: Color,
  scale: Float,
): PlatformRenderEffect? = null

internal actual fun createNoiseEffect(
  context: PlatformContext,
  noiseFactor: Float,
  mask: Shader?,
  scale: Float,
): PlatformRenderEffect {
  val noiseEffect = createShaderRenderEffect(NOISE_SHADER).withAlpha(noiseFactor)

  return when {
    mask != null -> {
      createBlendRenderEffect(
        blendMode = BlendMode.SrcIn,
        background = createShaderRenderEffect(mask),
        foreground = noiseEffect,
      )
    }

    else -> {
      noiseEffect
    }
  }
}
