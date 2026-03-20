import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.terrakok.App
import com.github.terrakok.FileInbox
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
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
        val windowState = rememberWindowState(width = 1300.dp, height = 900.dp)

        Window(
            title = "Mach-O viewer",
            state = windowState,
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(1300, 900)

            App()
        }
    }
}

