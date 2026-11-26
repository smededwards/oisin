package com.hourlynotice.wear

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.hourlynotice.shared.Schedule

class ScheduleDataService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            if (event.dataItem.uri.path?.startsWith("/hourly_notice/test/") == true) {
                val pattern = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("pattern")
                AlertReceiver.showTestNotification(this, pattern)
                return@forEach
            }
            if (event.dataItem.uri.path != "/hourly_notice/schedule") return@forEach
            val value = DataMapItem.fromDataItem(event.dataItem).dataMap.getString("value")
            val schedule = Schedule.decode(value)
            WatchStorage.save(this, schedule)
            WatchScheduler.reschedule(this, schedule)
        }
    }
}
