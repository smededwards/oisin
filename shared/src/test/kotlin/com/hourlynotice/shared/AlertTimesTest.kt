package com.hourlynotice.shared

import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class AlertTimesTest {
    @Test
    fun findsNextHalfHourlyAlertOnAnEnabledDay() {
        val schedule = Schedule(startMinutes = 9 * 60, endMinutes = 17 * 60, daysMask = 0b0000001, intervalMinutes = 30)
        val now = ZonedDateTime.of(2026, 11, 16, 9, 10, 0, 0, ZoneId.of("Europe/London"))
        val next = AlertTimes.nextAlert(now, schedule)
        assertEquals(9 * 60 + 30, next?.hour?.times(60)?.plus(next.minute))
    }

    @Test
    fun includesTheEndTimeAsTheFinalAlert() {
        val schedule = Schedule(startMinutes = 9 * 60, endMinutes = 17 * 60, daysMask = 0b0000001, intervalMinutes = 30)
        val now = ZonedDateTime.of(2026, 11, 16, 16, 45, 0, 0, ZoneId.of("Europe/London"))
        val next = AlertTimes.nextAlert(now, schedule)
        assertEquals(17 * 60, next?.hour?.times(60)?.plus(next.minute))
    }
}
