import io.github.kdroidfilter.nucleus.desktop.application.dsl.CompressionLevel
import io.github.kdroidfilter.nucleus.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.nucleus)
    alias(libs.plugins.kotlin.jvm)
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
    implementation(libs.nucleus.aot.runtime)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
}

nucleus {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "Mach-O viewer"
            packageVersion = project.findProperty("appVersion")?.toString() ?: "1.0.0"

            buildTypes.release.proguard {
                version = "7.8.1"
                isEnabled = true
                optimize = true
                joinOutputJars.set(true)
                configurationFiles.from(project.file("proguard-rules.pro"))
            }

            compressionLevel = CompressionLevel.Maximum
            cleanupNativeLibs = true
            enableAotCache = true

            modules("java.instrument", "jdk.unsupported")

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
}
