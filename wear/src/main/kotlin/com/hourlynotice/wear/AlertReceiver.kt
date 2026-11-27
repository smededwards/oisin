package com.hourlynotice.wear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.os.Vibrator
import com.hourlynotice.shared.Schedule
import com.hourlynotice.shared.VibrationPatterns
import java.time.LocalDateTime

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val schedule = WatchStorage.load(context)
        val now = LocalDateTime.now()
        if (schedule.isActiveAt(now)) {
            val pattern = when (schedule.intervalMinutes) {
                60 -> VibrationPatterns.byId("hourly_notice_five_pulse").timings
                30 -> VibrationPatterns.byId("triple_short").timings
                else -> VibrationPatterns.byId("double_short").timings
            }
            vibrate(context, pattern)
        }
        WatchScheduler.reschedule(context, schedule)
    }

    companion object {
        fun showTestNotification(context: Context, patternId: String?) {
            vibrate(context, VibrationPatterns.byId(patternId).timings)
        }

        private fun vibrate(context: Context, pattern: LongArray) {
            context.getSystemService(Vibrator::class.java).vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    pattern.map { if (it == 0L) 0 else 255 }.toIntArray(),
                    -1,
                ),
                VibrationAttributes.Builder()
                    .setUsage(VibrationAttributes.USAGE_ALARM)
                    .build(),
            )
        }
    }
}
