package org.akaii.kettles8.emulator.memory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import org.akaii.kettles8.emulator.display.DefaultFont
import kotlin.random.Random

class MemorySuite : FunSpec({

    val fakeROM: UByteArray = (0..<Address.ROM_BLOCK_SIZE).map { (it % 255).toUByte() }.toUByteArray()

    test("Initialization") {
        val initialized = Memory()
        val fontBlock = initialized.slice(Address.FONT_BLOCK)
        val romBlock = initialized.slice(Address.ROM_BLOCK)

        fontBlock shouldBe DefaultFont.representation
        romBlock shouldBe UByteArray(Address.ROM_BLOCK_SIZE)
    }

    test("Setting ROM into Memory") {
        val memory = Memory()
        val loadedROM = fakeROM.take(1000).toUByteArray()
        memory.setFromROM(loadedROM)
        val fontBlock = memory.slice(Address.FONT_BLOCK)
        val romBlock = memory.slice(Address.ROM_BLOCK)

        fontBlock shouldBe DefaultFont.representation
        romBlock.take(loadedROM.size) shouldBe loadedROM
        romBlock.drop(loadedROM.size) shouldContainOnly listOf(0u)

        memory.getInstruction(Address.ROM_START) shouldBe 1u
    }

    test("Setting Max-Sized ROM into Memory") {
        val memory = Memory()
        memory.setFromROM(fakeROM)
        val fontBlock = memory.slice(Address.FONT_BLOCK)
        val romBlock = memory.slice(Address.ROM_BLOCK)

        fontBlock shouldBe DefaultFont.representation
        romBlock shouldBe fakeROM

        memory[Address.ROM_END.toUInt()] shouldBe fakeROM.last()
    }

    test("Reset") {
        val memory = Memory()
        memory.setFromROM(fakeROM)

        (0..<Address.ROM_BLOCK_SIZE).forEach { index ->
            memory[index.toUInt()] = (Random(0).nextInt() % 255).toUByte()
        }

        val afterChanges = memory.slice(Address.ROM_BLOCK)
        afterChanges shouldNotBe fakeROM

        memory.reset()
        val afterReset = memory.slice(Address.ROM_BLOCK)
        afterReset shouldBe fakeROM
    }
})