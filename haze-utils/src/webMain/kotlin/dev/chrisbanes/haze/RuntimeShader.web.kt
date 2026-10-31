// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.haze

private const val NO_RUNTIME_SHADERS = "The web has no way to run a runtime shader"

/** Never made: browsers don't run SkSL, see [isRuntimeShaderRenderEffectSupported]. */
@InternalHazeApi
public actual class PlatformRuntimeEffect private constructor()

@InternalHazeApi
public actual fun createRuntimeEffect(sksl: String): PlatformRuntimeEffect =
  throw UnsupportedOperationException(NO_RUNTIME_SHADERS)

/** Returns `false` because browsers don't run SkSL. */
@InternalHazeApi
public actual fun isRuntimeShaderRenderEffectSupported(): Boolean = false

@InternalHazeApi
public actual fun createRuntimeShaderRenderEffect(
  effect: PlatformRuntimeEffect,
  shaderNames: Array<String>,
  inputs: Array<PlatformRenderEffect?>,
  uniforms: RuntimeShaderUniformProvider.() -> Unit,
): PlatformRenderEffect = throw UnsupportedOperationException(NO_RUNTIME_SHADERS)

@InternalHazeApi
public actual fun createMutableRuntimeShaderRenderEffect(
  effect: PlatformRuntimeEffect,
  shaderNames: Array<String>,
  inputs: Array<PlatformRenderEffect?>,
): MutableRuntimeShaderRenderEffect = throw UnsupportedOperationException(NO_RUNTIME_SHADERS)
