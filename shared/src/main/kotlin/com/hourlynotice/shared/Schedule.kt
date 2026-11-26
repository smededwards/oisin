package com.hourlynotice.shared

import java.time.LocalDateTime

data class Schedule(
    val active: Boolean = true,
    val startMinutes: Int = 9 * 60,
    val endMinutes: Int = 17 * 60,
    val daysMask: Int = 0b0011111,
    val intervalMinutes: Int = 60,
    val paused: Boolean = false,
) {
    fun isValid(): Boolean =
        startMinutes in 0..1439 && endMinutes in 1..1440 && startMinutes < endMinutes && daysMask != 0 && intervalMinutes in setOf(15, 30, 60)

    fun isActiveAt(now: LocalDateTime): Boolean {
        val day = 1 shl (now.dayOfWeek.value - 1)
        val minute = now.hour * 60 + now.minute
        return active && !paused && daysMask and day != 0 && minute in startMinutes until endMinutes
    }

    fun encode(): String = listOf(
        active,
        startMinutes,
        endMinutes,
        daysMask,
        intervalMinutes,
        paused,
    ).joinToString("|")

    companion object {
        fun decode(value: String?): Schedule {
            if (value == null) return Schedule()
            return runCatching {
                val parts = value.split('|')
                require(parts.size == 6)
                Schedule(
                    active = parts[0].toBooleanStrict(),
                    startMinutes = parts[1].toInt(),
                    endMinutes = parts[2].toInt(),
                    daysMask = parts[3].toInt(),
                    intervalMinutes = parts[4].let {
                        when (it) {
                            "true" -> 30
                            "false" -> 60
                            else -> it.toInt()
                        }
                    },
                    paused = parts[5].toBooleanStrict(),
                ).also { require(it.isValid()) }
            }.getOrDefault(Schedule())
        }
    }
}
