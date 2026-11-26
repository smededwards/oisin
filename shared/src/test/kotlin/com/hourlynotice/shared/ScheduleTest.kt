package com.hourlynotice.shared

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScheduleTest {
    @Test
    fun roundTripsAndChecksActiveWindow() {
        val schedule = Schedule(startMinutes = 9 * 60, endMinutes = 17 * 60, daysMask = 0b0000001)
        assertEquals(schedule, Schedule.decode(schedule.encode()))
        assertTrue(schedule.isActiveAt(LocalDateTime.of(2026, 11, 16, 10, 0)))
        assertFalse(schedule.isActiveAt(LocalDateTime.of(2026, 11, 17, 10, 0)))
        assertFalse(schedule.isActiveAt(LocalDateTime.of(2026, 11, 16, 17, 0)))
    }
}
