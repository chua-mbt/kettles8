package org.akaii.kettles8.rom

import android.content.ContentResolver
import android.net.Uri

class AndroidROM(private val uri: Uri) {

    fun load(contentResolver: ContentResolver): UByteArray {
        val stream = contentResolver.openInputStream(uri)
        return stream?.readBytes()?.toUByteArray() ?: UByteArray(0)
    }
}