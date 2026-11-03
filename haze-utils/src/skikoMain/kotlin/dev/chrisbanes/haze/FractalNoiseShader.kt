// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.haze

import androidx.compose.ui.graphics.Shader

/**
 * Creates a fractal noise shader: Skia's where Compose draws with Skia, a tile of seeded noise on
 * the web, which has none.
 */
@InternalHazeApi
public expect fun createFractalNoiseShader(
  baseFrequencyX: Float,
  baseFrequencyY: Float,
  numOctaves: Int,
  seed: Float,
): Shader
