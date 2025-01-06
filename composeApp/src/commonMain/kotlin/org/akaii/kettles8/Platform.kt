package org.akaii.kettles8

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform