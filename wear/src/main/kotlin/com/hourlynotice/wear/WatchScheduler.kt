package com.hourlynotice.wear

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.hourlynotice.shared.AlertTimes
import com.hourlynotice.shared.Schedule
import java.time.ZonedDateTime

internal object WatchScheduler {
    private const val REQUEST_CODE = 2400

    fun reschedule(context: Context, schedule: Schedule = WatchStorage.load(context)) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = pendingIntent(context)
        alarmManager.cancel(pendingIntent)
        if (!schedule.active || schedule.paused || !schedule.isValid()) return

        val next = AlertTimes.nextAlert(ZonedDateTime.now(), schedule) ?: return
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), pendingIntent)
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), pendingIntent)
        }
    }

    private fun pendingIntent(context: Context) = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, AlertReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

}
