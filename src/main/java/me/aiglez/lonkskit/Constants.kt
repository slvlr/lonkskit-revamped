package me.aiglez.lonkskit

import java.lang.UnsupportedOperationException

class Constants private constructor() {
    companion object {
        const val THROWABLE_LORE = "throwable"
        const val ATTACKER_TAG_EXPIRING: Long = 1
        const val POINTS_PER_KILL = 5
    }
    init {
        throw UnsupportedOperationException()
    }
}