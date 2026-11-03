// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalComposeUiApi::class)

package dev.chrisbanes.haze.blur

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.webcanvas.transformedShader

internal actual fun ShaderBrush.createScaledShader(size: Size, scale: Float): Shader {
  val shader = createShader(size)
  return if (scale == 1f) {
    shader
  } else {
    transformedShader(shader, Matrix().apply { scale(scale, scale) })
  }
}
