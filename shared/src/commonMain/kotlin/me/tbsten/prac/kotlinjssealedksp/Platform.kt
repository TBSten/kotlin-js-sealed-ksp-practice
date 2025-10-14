package me.tbsten.prac.kotlinjssealedksp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform