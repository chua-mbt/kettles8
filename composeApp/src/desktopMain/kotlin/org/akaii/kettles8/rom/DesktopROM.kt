package org.akaii.kettles8.rom

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

class DesktopROM(private val path: Path) {

    suspend fun load(): UByteArray =
        withContext(Dispatchers.IO) { path.toFile().readBytes().toUByteArray() }

}