// Copyright 2026, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(InternalHazeApi::class)

package dev.chrisbanes.haze

import androidx.compose.ui.graphics.ColorFilter

@InternalHazeApi
public actual fun ColorFilter.toPlatformColorFilter(): PlatformColorFilter =
  PlatformColorFilter(this)
