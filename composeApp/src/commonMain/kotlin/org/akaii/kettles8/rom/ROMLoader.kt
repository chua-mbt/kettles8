package org.akaii.kettles8.rom

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

class ROMLoader(private val path: Path) {

    companion object {
        val ROM_EXTENSION = "ch8"
    }

    suspend fun load(): UByteArray =
        withContext(Dispatchers.IO) { path.toFile().readBytes().toUByteArray() }

}