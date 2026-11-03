// Copyright 2023, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.haze.blur

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeShader
import androidx.compose.ui.graphics.skiaShader
import org.jetbrains.skia.Matrix33

internal actual fun ShaderBrush.createScaledShader(size: Size, scale: Float): Shader {
  val shader = createShader(size)
  return if (scale == 1f) {
    shader
  } else {
    shader.skiaShader
      .makeWithLocalMatrix(Matrix33.makeScale(scale, scale))
      .asComposeShader()
  }
}
