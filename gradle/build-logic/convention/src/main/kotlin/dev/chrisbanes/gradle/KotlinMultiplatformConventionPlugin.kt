// Copyright 2023, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.chrisbanes.gradle

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")

    configureJava()

    kotlin {
      applyDefaultHierarchyTemplate()

      compilerOptions {
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xexpect-actual-classes")
      }

      targets.withType<KotlinMultiplatformAndroidLibraryTarget> {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_11)
        }
      }

      targets.withType<KotlinJvmTarget> {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_11)
        }
      }
    }

    tasks.withType<KotlinNativeCompile>().configureEach {
      compilerOptions {
        // Kotlin emits unsuppressable duplicate KLIB loader warnings for the
        // AndroidX/JetBrains Compose metadata mix used by native targets.
        allWarningsAsErrors.set(false)
      }
    }

    tasks.withType<KotlinJsTest>().configureEach {
      enabled = false
    }

    useComposeWebCanvasOnTheWeb()

    configureSpotless()
  }
}

/**
 * Resolves Compose to the fork that draws with the browser for the web targets, which have no Skia
 * to draw with. The other targets stay on the Compose of the catalog.
 */
private fun Project.useComposeWebCanvasOnTheWeb() {
  val version = libs.findVersion("compose-web-canvas").get().requiredVersion
  configurations.configureEach {
    if (name.startsWith("js") || name.startsWith("wasmJs") || name.startsWith("web")) {
      resolutionStrategy.eachDependency {
        if (requested.group in COMPOSE_WEB_CANVAS_GROUPS) {
          useVersion(version)
        }
      }
    }
  }
}

/** The groups that the fork of Compose publishes. */
private val COMPOSE_WEB_CANVAS_GROUPS = setOf(
  "org.jetbrains.compose.animation",
  "org.jetbrains.compose.foundation",
  "org.jetbrains.compose.material",
  "org.jetbrains.compose.material3",
  "org.jetbrains.compose.runtime",
  "org.jetbrains.compose.ui",
)

/**
 * Whether the Apple (iOS + macOS) Kotlin/Native targets are configured for this build.
 *
 * Non-Apple hosts cannot build them, so CI opts out with `-Phaze.disableAppleTargets`.
 */
val Project.appleTargetsEnabled: Boolean
  get() = !providers.gradleProperty("haze.disableAppleTargets").isPresent

fun KotlinMultiplatformExtension.addDefaultHazeTargets(
  project: Project,
  withSkikoMain: Boolean = false,
  withWeb: Boolean = true,
) {
  jvm()

  if (project.appleTargetsEnabled) {
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
  }

  if (withWeb) {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
      browser()
      binaries.executable()
    }

    js {
      browser()
      binaries.executable()
    }
  }

  if (withSkikoMain) {
    // skikoMain has what every target but Android shares. skiaMain has what needs Skia itself,
    // which the web targets don't have: they draw with the browser.
    val skikoMain = sourceSets.maybeCreate("skikoMain").apply {
      dependsOn(sourceSets.getByName("commonMain"))
    }
    val skiaMain = sourceSets.maybeCreate("skiaMain").apply {
      dependsOn(skikoMain)
    }
    if (project.appleTargetsEnabled) {
      // appleMain is the common parent of iosMain and macosMain, both of which render via Skiko.
      sourceSets.getByName("appleMain").dependsOn(skiaMain)
    }
    sourceSets.getByName("jvmMain").dependsOn(skiaMain)
    if (withWeb) {
      sourceSets.getByName("wasmJsMain").dependsOn(skikoMain)
      sourceSets.getByName("jsMain").dependsOn(skikoMain)
    }
  }
}

internal fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}

internal val Project.kotlin: KotlinMultiplatformExtension
  get() = extensions.getByType<KotlinMultiplatformExtension>()
