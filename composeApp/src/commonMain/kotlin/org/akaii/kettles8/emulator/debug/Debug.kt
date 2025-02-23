package org.akaii.kettles8.emulator.debug

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.*
import org.akaii.kettles8.emulator.cpu.Cpu

object Debug {
    val panelVisible: MutableState<Boolean> = mutableStateOf(false)
    var sampling: (() -> Unit)? = null
    var maxCycle: Int = Int.MAX_VALUE

    fun flip(cpu: Cpu) {
        maxCycle = if (panelVisible.value) Int.MAX_VALUE else cpu.cycles
        panelVisible.value = !panelVisible.value
    }

    fun step(keyEvent: KeyEvent): Boolean {
        val stepInput = keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Spacebar && panelVisible.value
        if (stepInput) { maxCycle++ }
        return stepInput
    }

    fun reset() {
        maxCycle = if (panelVisible.value) 0 else Int.MAX_VALUE
    }
}
