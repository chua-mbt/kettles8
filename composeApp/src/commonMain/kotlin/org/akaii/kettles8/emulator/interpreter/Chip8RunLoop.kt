package org.akaii.kettles8.emulator.interpreter

import io.github.oshai.kotlinlogging.KotlinLogging
import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.instructions.Instruction
import org.akaii.kettles8.emulator.memory.Memory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Chip8RunLoop : Interpreter {
    companion object {
        private const val MICROSECONDS_IN_SECOND = 1_000_000L
        private const val CPU_TICKS_IN_SECOND = 500L
        private const val TIMER_TICKS_IN_SECOND = 60L
        const val CPU_TICKS_IN_MICROSECOND = MICROSECONDS_IN_SECOND / CPU_TICKS_IN_SECOND
        const val TIMER_TICKS_IN_MICROSECOND = MICROSECONDS_IN_SECOND / TIMER_TICKS_IN_SECOND
    }

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val logger = KotlinLogging.logger {}

    override fun start(
        cpu: Cpu,
        memory: Memory,
        display: Display,
        keypad: Keypad
    ) {
        executor.scheduleAtFixedRate(
            Runnable(cpu::updateTimers), 0, TIMER_TICKS_IN_MICROSECOND,
            TimeUnit.MICROSECONDS
        )

        executor.scheduleAtFixedRate(
            Runnable { cpuTick(cpu, memory, display, keypad) }, 0, CPU_TICKS_IN_MICROSECOND,
            TimeUnit.MICROSECONDS
        )
    }

    fun cpuTick(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (keypad.futureInput.isPending()) {
            keypad.futureInput.checkInput(keypad)
        } else {
            if (cpu.running/* && cpu.cycles < 5*/) {
                cpu.cycles += 1
                val instruction = fetchInstruction(cpu, memory)
                cpu.advanceProgram()
                instruction.execute(cpu, memory, display, keypad)
                keypad.clearPrevious()
                logCycle(instruction, cpu)
            }
        }
    }

    private fun logCycle(instruction: Instruction, cpu: Cpu) {
        val debugForCycle = listOf(
            "",
            "*".repeat(30),
            instruction.description(),
            cpu.toString(),
        )
        logger.debug { debugForCycle.joinToString("\n") }
    }

    override fun cleanup() {
        executor.shutdownNow()
    }

    fun fetchInstruction(cpu: Cpu, memory: Memory): Instruction =
        Instruction.Companion.decode(memory.getInstruction(cpu.programCounter))
}