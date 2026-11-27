package com.hourlynotice.shared

data class VibrationPattern(
    val id: String,
    val label: String,
    val timings: LongArray,
)

object VibrationPatterns {
    val all = listOf(
        VibrationPattern("single_short", "Single short", longArrayOf(0, 150)),
        VibrationPattern("single_long", "Single long", longArrayOf(0, 1000)),
        VibrationPattern("double_short", "Double short", longArrayOf(0, 150, 200, 150)),
        VibrationPattern("triple_short", "Triple short", longArrayOf(0, 150, 200, 150, 200, 150)),
        VibrationPattern(
            "hourly_notice_five_pulse",
            "Hourly: four pulses",
            longArrayOf(0, 150, 200, 150, 200, 150, 200, 150),
        ),
        VibrationPattern("short_long", "Short then long", longArrayOf(0, 180, 150, 1000)),
        VibrationPattern("long_short", "Long then short", longArrayOf(0, 1000, 150, 180)),
        VibrationPattern("double_long", "Double long", longArrayOf(0, 1000, 150, 1000)),
        VibrationPattern("escalating", "Escalating three", longArrayOf(0, 120, 100, 250, 100, 500)),
    )

    fun byId(id: String?): VibrationPattern = all.firstOrNull { it.id == id } ?: all[1]
}
