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

# Keep Jewel and its JetBrains dependencies
-keep class org.jetbrains.jewel.** { *; }
-keep class com.jetbrains.** { *; }

# Keep Nucleus classes
-keep class io.github.kdroidfilter.nucleus.** { *; }

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
