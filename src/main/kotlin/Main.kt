import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kettles8.emulator.Emulator
import kettles8.emulator.display.Display
import kotlinx.coroutines.delay
import kotlin.random.Random


fun main() = application {
    val emulator = Emulator()
    emulator.display.fill()
    Window(onCloseRequest = ::exitApplication, title = "Kettles-8") {
        emulator.display.render()
        LaunchedEffect(Unit) {
            while(true) {
                emulator.display.flip(Random.nextInt(Display.DISPLAY_WIDTH), Random.nextInt(Display.DISPLAY_HEIGHT))
                delay(1000)
            }
        }
    }
}
