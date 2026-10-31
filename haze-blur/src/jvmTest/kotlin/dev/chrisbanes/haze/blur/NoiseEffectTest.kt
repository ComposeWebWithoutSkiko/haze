// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.haze.blur

import assertk.assertThat
import assertk.assertions.isNotNull
import dev.chrisbanes.haze.PlatformContext
import kotlin.test.Test

class NoiseEffectTest {

  @Test
  fun noiseFactorJustBelowOne_makesAnEffect() {
    val effect = createNoiseEffect(
      context = PlatformContext.INSTANCE,
      noiseFactor = 0.999f,
      mask = null,
      scale = 1f,
    )

    assertThat(effect).isNotNull()
  }
}
