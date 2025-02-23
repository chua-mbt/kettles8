package org.akaii.kettles8.emulator.cpu

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.akaii.kettles8.emulator.beep.Beep

class CpuSuite : FunSpec({
    test("Manage the sound timer") {
        val testBeep = object : Beep {
            var isBeeping = false

            override fun start() {
                isBeeping = true
            }

            override fun stop() {
                isBeeping = false
            }

            override fun cleanup() {}
        }

        val cpu = Cpu(beep = testBeep)
        testBeep.isBeeping shouldBe false
        cpu.soundTimer = 10u

        for(i in 1 until 10) {
            cpu.updateTimers()
            cpu.soundTimer shouldBe (10u - i.toUInt()).toUByte()
            testBeep.isBeeping shouldBe true
        }

        cpu.updateTimers()
        cpu.soundTimer shouldBe 0u
        testBeep.isBeeping shouldBe true

        cpu.updateTimers()
        cpu.soundTimer shouldBe 0u
        testBeep.isBeeping shouldBe false

        repeat(10) {
            cpu.updateTimers()
            cpu.soundTimer shouldBe 0u
            testBeep.isBeeping shouldBe false
        }
    }
})