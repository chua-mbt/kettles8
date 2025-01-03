package kettles8.emulator.memory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import kettles8.emulator.DefaultFont
import kettles8.rom.ROMLoader
import kotlin.io.path.toPath

class MemorySuite : FunSpec({

    suspend fun loadResourceROM(romName: String): UByteArray {
        val path = javaClass.classLoader.getResource("kettles8/rom/$romName")!!.toURI().toPath()
        return ROMLoader(path).load()
    }

    test("Initialization") {
        val initialized = Memory()
        val fontBlock = initialized.slice(Address.FONT_BLOCK)
        val romBlock = initialized.slice(Address.ROM_BLOCK)

        fontBlock shouldBe DefaultFont.representation
        romBlock shouldBe UByteArray(Address.ROM_BLOCK_SIZE)
    }

    test("Setting ROM into Memory") {
        val memory = Memory()
        val loadedRom = loadResourceROM("1-chip8-logo.ch8")
        memory.setFromROM(loadedRom)
        val fontBlock = memory.slice(Address.FONT_BLOCK)
        val romBlock = memory.slice(Address.ROM_BLOCK)

        fontBlock shouldBe DefaultFont.representation
        romBlock.take(loadedRom.size) shouldBe loadedRom
        romBlock.drop(loadedRom.size) shouldContainOnly listOf(0u)

        memory.fetch(Address.ROM_START) shouldBe 0x00E0u
    }
})