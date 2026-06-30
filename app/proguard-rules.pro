-dontwarn androidx.compose.material.**
-dontwarn androidx.compose.ui.res.**
-dontwarn org.jetbrains.jewel.foundation.lazy.SelectableLazyItemScopeDelegate
-dontwarn org.jetbrains.jewel.ui.component.IconKt
-dontwarn com.sun.jna.**
-dontwarn org.jetbrains.jewel.window.utils.macos.MacUtil
-dontnote com.sun.jna.**
-dontnote org.jetbrains.jewel.window.utils.macos.MacUtil

# Keep JNA classes
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.Structure { *; }

# Suppress version-mismatch warnings between Jewel 0.37.0 and Compose 1.11.x
-dontwarn org.jetbrains.jewel.intui.standalone.popup.JDialogRendererKt
-dontwarn org.jetbrains.jewel.ui.component.TextContextMenu
-dontwarn org.jetbrains.jewel.ui.graphics.CssLinearGradientBrush
-dontwarn androidx.compose.ui.awt.ComposePanel
-dontwarn androidx.compose.foundation.text.TextContextMenu$TextManager
-dontwarn androidx.compose.ui.graphics.ShaderKt

# Keep Jewel and its JetBrains dependencies
-keep class org.jetbrains.jewel.** { *; }
-keep class com.jetbrains.** { *; }

# Keep Nucleus classes
-keep class io.github.kdroidfilter.nucleus.** { *; }

# Suppress warnings for platform-specific native bridges not present in this build
-dontwarn io.github.kdroidfilter.nucleus.window.utils.macos.NativeMacBridge
-dontwarn io.github.kdroidfilter.nucleus.nativessl.mac.NativeSslBridge
-dontwarn io.github.kdroidfilter.nucleus.nativessl.windows.WindowsSslBridge
-dontwarn io.github.kdroidfilter.nucleus.energymanager.macos.NativeMacOsEnergyBridge
-dontwarn io.github.kdroidfilter.nucleus.energymanager.linux.NativeLinuxEnergyBridge
-dontwarn io.github.kdroidfilter.nucleus.energymanager.windows.NativeWindowsBridge
-dontwarn io.github.kdroidfilter.nucleus.hidpi.HiDpiLinuxBridge
-dontwarn io.github.kdroidfilter.nucleus.notification.windows.NativeWindowsNotificationBridge
-dontwarn io.github.kdroidfilter.nucleus.globalhotkey.windows.NativeWindowsHotKeyBridge
-dontwarn io.github.kdroidfilter.nucleus.globalhotkey.macos.NativeMacOsHotKeyBridge
-dontwarn io.github.kdroidfilter.nucleus.globalhotkey.linux.NativeLinuxHotKeyBridge
-dontwarn io.github.kdroidfilter.nucleus.launcher.windows.NativeWindowsBadgeBridge
-dontwarn io.github.kdroidfilter.nucleus.launcher.windows.NativeWindowsJumpListBridge
-dontwarn io.github.kdroidfilter.nucleus.launcher.windows.NativeWindowsTaskbarBridge
-dontwarn io.github.kdroidfilter.nucleus.launcher.windows.ThumbBarClickListener

# Keep JewelLogger's dynamically referenced classes
-dontwarn com.intellij.openapi.diagnostic.Logger
-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.Logger
-dontnote org.jetbrains.jewel.foundation.util.JewelLogger**

# Keep AWT methods accessed via reflection in JNA Platform WindowUtils
-keepclassmembers class java.awt.Component {
    public * getPeer();
}
-keepclassmembers class java.awt.Window {
    public void setAlpha(float);
}

# Keep fields used by reflection in PlatformUtils.kt
-keepclassmembers class androidx.compose.ui.draganddrop.DragAndDropEvent {
    *;
}
