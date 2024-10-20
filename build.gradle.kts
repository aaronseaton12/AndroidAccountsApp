// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // STEP 1: Apply the Kotlin JVM (or Kotlin Android plugin)
    id ("org.jetbrains.kotlin.jvm") version "1.9.0"
    // STEP 2: Apply the KSP plugin
    id ("com.google.devtools.ksp")version "2.0.0-1.0.24"
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

}
