import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.terrakok.App
import com.github.terrakok.FileInbox
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.styling.TitleBarStyle
import java.awt.Desktop
import java.awt.Dimension

fun main(args: Array<String>) {
    args.firstOrNull()?.let { FileInbox.send(it) }
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE)) {
        Desktop.getDesktop().setOpenFileHandler { event ->
            event.files.firstOrNull()?.let { file ->
                FileInbox.send(file.absolutePath)
            }
        }
    }
    application {
        val systemIsDark = isSystemInDarkTheme()
        val theme = if (systemIsDark) JewelTheme.darkThemeDefinition()
        else JewelTheme.lightThemeDefinition()
        IntUiTheme(
            theme = theme,
            styling = ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.dark()
            ),
        ) {
            val windowState = rememberWindowState(width = 1300.dp, height = 900.dp)
            DecoratedWindow(
                title = "Mach-O viewer",
                state = windowState,
                onCloseRequest = ::exitApplication,
            ) {
                window.minimumSize = Dimension(1300, 900)
                App()
            }
        }
    }
}

