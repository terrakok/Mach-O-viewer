import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
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
    implementation(libs.jewelWindow)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "Mach-O viewer"
            packageVersion = project.findProperty("appVersion")?.toString() ?: "1.0.0"

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
