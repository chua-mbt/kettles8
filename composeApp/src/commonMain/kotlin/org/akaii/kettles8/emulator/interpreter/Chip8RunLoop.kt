package org.akaii.kettles8.emulator.interpreter

import io.github.oshai.kotlinlogging.KotlinLogging
import org.akaii.kettles8.emulator.CPU
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
        cpu: CPU,
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

    private fun cpuTick(cpu: CPU, memory: Memory, display: Display, keypad: Keypad) {
        if (keypad.futureInput.isPending()) {
            keypad.futureInput.checkInput(keypad)
        } else {
            val instruction = fetchInstruction(cpu, memory)
            logger.debug { instruction.description() }
            cpu.advanceProgram()
            instruction.execute(cpu, memory, display, keypad)
            keypad.clearPrevious()
        }
    }

    override fun stop() {
        executor.shutdownNow()
    }

    fun fetchInstruction(cpu: CPU, memory: Memory): Instruction =
        Instruction.Companion.decode(memory[cpu.programCounter])
}