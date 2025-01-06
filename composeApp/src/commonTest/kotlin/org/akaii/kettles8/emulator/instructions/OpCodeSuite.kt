package org.akaii.kettles8.emulator.instructions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OpCodeSuite : FunSpec({
    test("OpCode Sanity Check") {
        OpCode.entries.size shouldBe 35
    }
})