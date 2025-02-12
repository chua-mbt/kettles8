package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.debug.Debug
import org.akaii.kettles8.emulator.format.Hex
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register

@Composable
fun DebugPanel(cpu: Cpu, keypad: Keypad, setTitle: (String) -> Unit) {
    val samples = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { Debug.sampling = { samples.value++ } }
    DisposableEffect(Unit) { onDispose { Debug.sampling = null } }
    samples.value

    setTitle("Cycle: ${cpu.cycles}${cpu.instruction?.let { " | Instruction: ${it.description()}" } ?: ""}")

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
            Column(modifier = Modifier.weight(2f).padding(end = 8.dp).fillMaxHeight()) {
                Section("Registers") {
                    GridDisplay(
                        Register.entries.map { it.toString() to cpu.registers[it].toHexString(Hex.UI8_FORMAT) },
                        columns = 4
                    )
                }
            }

            Divider(color = Color.Gray, modifier = Modifier.fillMaxHeight().width(1.dp))

            Column(modifier = Modifier.weight(1.5f).padding(end = 8.dp).fillMaxHeight()) {
                Section("CPU") {
                    GridDisplay(
                        listOf(
                            "PC" to cpu.programCounter.toHexString(Hex.UI16_FORMAT),
                            "I" to cpu.indexRegister.toHexString(Hex.UI16_FORMAT),
                            "DT" to cpu.delayTimer.toString(),
                            "ST" to cpu.soundTimer.toString()
                        ), columns = 2
                    )
                }

                Divider(color = Color.Gray, thickness = 1.dp)
                Section("Keypad") {
                    GridDisplay(
                        Key.entries.chunked(8).map { row ->
                            row.map { it.name to if (keypad[it]) "●" else "○" }
                        }.flatten(),
                        columns = 8, fontSize = 12.sp
                    )
                }
            }

            Divider(color = Color.Gray, modifier = Modifier.fillMaxHeight().width(1.dp))

            Column(modifier = Modifier.weight(2f).fillMaxHeight()) {
                Section("Stack") {
                    GridDisplay(
                        cpu.stack.reversed()
                            .mapIndexed { i, v -> "S${cpu.stack.lastIndex - i}" to v.toHexString(Hex.UI8_FORMAT) },
                        columns = 4
                    )
                }
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun GridDisplay(pairs: List<Pair<String, String>>, columns: Int, fontSize: TextUnit = 14.sp) {
    Column(modifier = Modifier.fillMaxWidth()) {
        pairs.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                row.forEach { (label, value) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            label,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = fontSize
                        )
                        Text(value, fontFamily = FontFamily.Monospace, fontSize = fontSize)
                    }
                }
            }
        }
    }
}