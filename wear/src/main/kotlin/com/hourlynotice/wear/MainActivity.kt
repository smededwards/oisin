package com.hourlynotice.wear

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hourlynotice.shared.Schedule

class MainActivity : ComponentActivity() {
    private var schedule by mutableStateOf(Schedule())
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshSchedule()
        if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { OisinTheme { WatchScreen(schedule) { togglePause() } } }
    }

    override fun onResume() {
        super.onResume()
        refreshSchedule()
    }

    private fun refreshSchedule() {
        schedule = WatchStorage.load(this)
        WatchScheduler.reschedule(this, schedule)
    }

    private fun togglePause() {
        schedule = schedule.copy(paused = !schedule.paused)
        WatchStorage.save(this, schedule)
        WatchScheduler.reschedule(this, schedule)
    }
}

@androidx.compose.runtime.Composable
private fun WatchScreen(schedule: Schedule, onTogglePause: () -> Unit) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 30.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(
                    painter = painterResource(com.hourlynotice.wear.R.drawable.ic_launcher),
                    contentDescription = "Oisin icon",
                    modifier = Modifier.size(32.dp),
                )
                Text("Oisin", style = MaterialTheme.typography.titleMedium)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(if (schedule.paused || !schedule.active) "Paused" else "Active", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text("%02d:%02d–%02d:%02d".format(schedule.startMinutes / 60, schedule.startMinutes % 60, schedule.endMinutes / 60, schedule.endMinutes % 60))
                    Text("Every ${schedule.intervalMinutes} minutes")
                }
            }
            Button(modifier = Modifier.fillMaxWidth().height(40.dp), onClick = onTogglePause) { Text(if (schedule.paused) "Resume" else "Pause") }
        }
    }
}
