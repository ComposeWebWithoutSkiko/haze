// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalHazeApi::class, InternalComposeUiApi::class, ExperimentalWasmJsInterop::class)

package dev.chrisbanes.haze

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.webcanvas.composeImageBitmapOf
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

/**
 * A tile of noise, as on Android: Canvas 2D has no fractal noise. It's made once, from [seed], in
 * the browser; [baseFrequencyX], [baseFrequencyY] and [numOctaves] don't apply to it.
 */
@InternalHazeApi
public actual fun createFractalNoiseShader(
  baseFrequencyX: Float,
  baseFrequencyY: Float,
  numOctaves: Int,
  seed: Float,
): Shader = ImageShader(
  image = composeImageBitmapOf(noiseTile(seed.toInt())),
  tileModeX = TileMode.Repeated,
  tileModeY = TileMode.Repeated,
)

@InternalHazeApi
public actual fun createBlendColorFilter(
  color: Int,
  blendMode: BlendMode,
): PlatformColorFilter = PlatformColorFilter(ColorFilter.tint(Color(color), blendMode))

/** A `<canvas>` of opaque random colors around the middle gray, which [seed] repeats. */
// language=js
private fun noiseTile(seed: Int): JsAny = js(
  """(() => {
    const size = 256;
    const canvas = document.createElement('canvas');
    canvas.width = size;
    canvas.height = size;
    const context = canvas.getContext('2d');
    const image = context.createImageData(size, size);
    const pixels = image.data;
    let state = seed;
    const next = () => {
      state = (Math.imul(state, 1664525) + 1013904223) | 0;
      return state >>> 24;
    };
    for (let i = 0; i < pixels.length; i += 4) {
      pixels[i] = (next() + next()) >> 1;
      pixels[i + 1] = (next() + next()) >> 1;
      pixels[i + 2] = (next() + next()) >> 1;
      pixels[i + 3] = 255;
    }
    context.putImageData(image, 0, 0);
    return canvas;
  })()""",
)
