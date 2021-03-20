import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.compose") version "0.4.0-build168"
    id("com.squareup.sqldelight") version "1.4.4"
}

sqldelight {
    database("BackupDatabase") {
        packageName = "com.example.database.git"
        dialect = "sqlite:3.24"
    }
}

group = "me.winda"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.4")
    implementation("junit:junit:4.12")
    testImplementation("com.google.truth:truth:1.1.2")
    testImplementation("com.squareup.sqldelight:sqlite-driver:1.4.4")
    implementation("org.kodein.di:kodein-di:7.4.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "database-backup"
        }
    }
}