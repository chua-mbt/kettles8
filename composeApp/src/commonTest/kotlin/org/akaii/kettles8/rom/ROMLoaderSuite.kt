package org.akaii.kettles8.rom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlin.io.path.toPath

class ROMLoaderSuite : FunSpec({

    suspend fun loadResourceROM(romName: String): UByteArray {
        val path = javaClass.classLoader.getResource("kettles8/rom/$romName")!!.toURI().toPath()
        return ROMLoader(path).load()
    }

    test("1-chip8-logo.ch8") {
        async { loadResourceROM("1-chip8-logo.ch8") }.await().size shouldBe 260
    }

    test("2-ibm-logo.ch8") {
        async { loadResourceROM("2-ibm-logo.ch8") }.await().size shouldBe 132
    }

    test("3-corax+.ch8") {
        async { loadResourceROM("3-corax+.ch8") }.await().size shouldBe 761
    }

    test("4-flags.ch8") {
        async { loadResourceROM("4-flags.ch8") }.await().size shouldBe 1041
    }
})