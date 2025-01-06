package org.akaii.kettles8.emulator.memory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AddressSuite : FunSpec({

    test("Address Block Size Sanity Check") {
        Address.FONT_BLOCK.toList().size shouldBe Address.FONT_BLOCK_SIZE
        Address.ROM_BLOCK.toList().size shouldBe Address.ROM_BLOCK_SIZE
    }
})