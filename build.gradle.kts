// Copyright 2023, Christopher Banes and the Haze project contributors
// SPDX-License-Identifier: Apache-2.0


import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

plugins {
  id("dev.chrisbanes.root")

  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.kotlin.multiplatform.library) apply false
  alias(libs.plugins.baselineprofile) apply false
  alias(libs.plugins.cacheFixPlugin) apply false
  alias(libs.plugins.android.lint) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.mavenpublish) apply false
  alias(libs.plugins.metalava) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.poko) apply false
  alias(libs.plugins.dokka)
}

val checkPublicApiDocumentation = tasks.register("checkPublicApiDocumentation") {
  group = "verification"
  description = "Checks that public API documentation generates without warnings."
}

tasks.named("check") {
  dependsOn(checkPublicApiDocumentation)
}

tasks.withType<KotlinNpmInstallTask>().configureEach {
  args.add("--userconfig=${rootProject.file("gradle/npmrc")}")
  if (name == "kotlinWasmNpmInstall") {
    // Kotlin supplies Wasm tooling separately; plugin peers must not install a second webpack.
    args.add("--omit=peer")
  }
}

subprojects {
  pluginManager.withPlugin("maven-publish") {
    extensions.configure<PublishingExtension> {
      repositories {
        maven {
          name = "ComposeRepo"
          setUrl(System.getenv("COMPOSE_REPO_URL"))
          credentials {
            username = System.getenv("COMPOSE_REPO_USERNAME")
            password = System.getenv("COMPOSE_REPO_KEY")
          }
        }
      }
    }
  }

  pluginManager.withPlugin("org.jetbrains.dokka") {
    extensions.configure<DokkaExtension> {
      dokkaPublications.configureEach {
        failOnWarning.set(true)
      }
      dokkaSourceSets.configureEach {
        reportUndocumented.set(true)
      }
    }

    pluginManager.withPlugin("dev.chrisbanes.metalava") {
      val dokkaModuleTask = tasks.named("dokkaGenerateModuleHtml")
      checkPublicApiDocumentation.configure {
        dependsOn(dokkaModuleTask)
      }
    }
  }

  dependencies {
    components {
      listOf(
        "coil-core-iosarm64",
        "coil-core-iossimulatorarm64",
        "coil-core-jvm",
        "coil-core-macosarm64",
      ).forEach { module ->
        withModule("io.coil-kt.coil3:$module") {
          allVariants {
            withDependencies {
              removeIf { it.group == "org.jetbrains.skiko" && it.name == "skiko" }
            }
          }
        }
      }
    }
  }
}
