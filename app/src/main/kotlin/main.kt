import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.terrakok.App
import java.awt.Dimension

fun main() = application {
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

