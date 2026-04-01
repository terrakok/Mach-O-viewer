import io.github.kdroidfilter.nucleus.desktop.application.dsl.CompressionLevel
import io.github.kdroidfilter.nucleus.desktop.application.dsl.PublishMode
import io.github.kdroidfilter.nucleus.desktop.application.dsl.ReleaseChannel
import io.github.kdroidfilter.nucleus.desktop.application.dsl.ReleaseType
import io.github.kdroidfilter.nucleus.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.nucleus)
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.jewelStandalone)
    implementation(libs.jna)
    implementation(libs.nucleus.decorated.window.jewel)
    implementation(libs.nucleus.decorated.window.jni)
    implementation(libs.nucleus.core.runtime)
    implementation(libs.nucleus.darkmode.detector)
    implementation(libs.nucleus.system.color)
    implementation(libs.nucleus.graalvm.runtime)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
}

nucleus.application {
    mainClass = "MainKt"

    graalvm {
        isEnabled = true
        imageName = "mach-o-viewer"
        javaLanguageVersion = 25
        jvmVendor = JvmVendorSpec.BELLSOFT
        nativeImageConfigBaseDir.set(project.file("src/graalvm"))
        buildArgs.addAll(
            "-H:+AddAllCharsets",
            "-Djava.awt.headless=false",
            "-Os",
        )

    }

    nativeDistributions {
        targetFormats(TargetFormat.Dmg)
        packageName = "Mach-O viewer"
        packageVersion = project.findProperty("appVersion")?.toString() ?: "1.0.0"
        compressionLevel = CompressionLevel.Maximum
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
        cleanupNativeLibs = true

        fileAssociation(
            mimeType = "application/x-mach-binary",
            extension = "dylib",
            description = "Dynamic Library",
        )
        fileAssociation(
            mimeType = "application/x-object",
            extension = "o",
            description = "Object File",
        )
        fileAssociation(
            mimeType = "application/x-archive",
            extension = "a",
            description = "Static Library",
        )
        fileAssociation(
            mimeType = "application/x-mach-bundle",
            extension = "bundle",
            description = "macOS Bundle",
        )
        fileAssociation(
            mimeType = "application/x-mach-binary",
            extension = "kexe",
            description = "Kotlin/Native Executable",
        )

        macOS {
            iconFile.set(project.file("appIcons/MacosIcon.icns"))
            bundleID = "com.github.terrakok.machoviewer"
            minimumSystemVersion = "13.0"
            infoPlist {
                extraKeysRawXml = """
                    <key>CFBundleDocumentTypes</key>
                    <array>
                        <dict>
                            <key>CFBundleTypeName</key>
                            <string>All Files</string>
                            <key>CFBundleTypeRole</key>
                            <string>Viewer</string>
                            <key>LSItemContentTypes</key>
                            <array>
                                <string>public.data</string>
                                <string>public.content</string>
                            </array>
                        </dict>
                    </array>
                """.trimIndent()
            }
        }
    }
}
