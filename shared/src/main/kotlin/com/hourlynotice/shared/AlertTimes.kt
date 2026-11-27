package com.hourlynotice.shared

import java.time.ZonedDateTime

object AlertTimes {
    fun nextAlert(now: ZonedDateTime, schedule: Schedule): ZonedDateTime? {
        for (dayOffset in 0..7) {
            val date = now.toLocalDate().plusDays(dayOffset.toLong())
            val dayBit = 1 shl (date.dayOfWeek.value - 1)
            if (schedule.daysMask and dayBit == 0) continue
            for (hour in 0..23) {
                for (minute in 0 until 60 step schedule.intervalMinutes) {
                    val candidate = date.atTime(hour, minute).atZone(now.zone)
                    val candidateMinute = hour * 60 + minute
                    if (candidate.isAfter(now) && candidateMinute in schedule.startMinutes..schedule.endMinutes) return candidate
                }
            }
        }
        return null
    }
}
