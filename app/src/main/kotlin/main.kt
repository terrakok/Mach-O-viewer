import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.terrakok.App
import com.github.terrakok.FileInbox
import io.github.kdroidfilter.nucleus.darkmodedetector.isSystemInDarkMode
import io.github.kdroidfilter.nucleus.graalvm.GraalVmInitializer
import io.github.kdroidfilter.nucleus.systemcolor.systemAccentColor
import io.github.kdroidfilter.nucleus.window.jewel.JewelDecoratedWindow
import org.jetbrains.jewel.foundation.BorderColors
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.OutlineColors
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.core.theme.IntUiDarkTheme
import org.jetbrains.jewel.intui.core.theme.IntUiLightTheme
import org.jetbrains.jewel.intui.standalone.theme.*
import org.jetbrains.jewel.ui.ComponentStyling
import java.awt.Desktop
import java.awt.Dimension

fun main(args: Array<String>) {
    GraalVmInitializer.initialize()
    args.firstOrNull()?.let { FileInbox.send(it) }
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE)) {
        Desktop.getDesktop().setOpenFileHandler { event ->
            event.files.firstOrNull()?.let { file ->
                FileInbox.send(file.absolutePath)
            }
        }
    }
    application {
        AppTheme {
            val windowState = rememberWindowState(width = 1300.dp, height = 900.dp)
            JewelDecoratedWindow(
                title = "Mach-O viewer",
                state = windowState,
                onCloseRequest = { exitApplication() },
            ) {
                window.minimumSize = Dimension(1300, 900)
                App()
            }
        }
    }
}

@Composable
fun AppTheme(
    accent: Color? = systemAccentColor(),
    isDark: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    IntUiTheme(
        theme = JewelThemeDefinition(accent, isDark),
        styling = ComponentStyling.default(),
        content = content,
    )
}

@Composable
private fun JewelThemeDefinition(accent: Color?, isDark: Boolean) = if (isDark) {
    JewelTheme.darkThemeDefinition(
        colors = GlobalColors.dark(
            borders = BorderColors.dark(focused = accent ?: IntUiDarkTheme.colors.gray(2)),
            outlines = OutlineColors.dark(focused = accent ?: IntUiDarkTheme.colors.blue(6)),
        )
    )
} else {
    JewelTheme.lightThemeDefinition(
        colors = GlobalColors.light(
            borders = BorderColors.light(focused = accent ?: IntUiLightTheme.colors.gray(14)),
            outlines = OutlineColors.light(focused = accent ?: IntUiLightTheme.colors.blue(4)),
        )
    )
}

