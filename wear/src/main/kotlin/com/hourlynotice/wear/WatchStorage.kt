package com.hourlynotice.wear

import android.content.Context
import com.hourlynotice.shared.Schedule

internal object WatchStorage {
    private const val PREFS = "schedule"
    private const val VALUE = "value"

    fun load(context: Context): Schedule =
        Schedule.decode(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(VALUE, null))

    fun save(context: Context, schedule: Schedule) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(VALUE, schedule.encode()).apply()
    }
}
