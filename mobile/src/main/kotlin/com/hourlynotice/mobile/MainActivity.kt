package com.hourlynotice.mobile

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.hourlynotice.shared.AlertTimes
import com.hourlynotice.shared.Schedule
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val preferences by lazy { getSharedPreferences("schedule", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OisinTheme {
                ScheduleScreen(
                    initial = loadSchedule(),
                    onSave = ::saveAndSync,
                )
            }
        }
    }

    private fun loadSchedule() = Schedule.decode(preferences.getString("value", null))

    private fun saveAndSync(schedule: Schedule) {
        preferences.edit().putString("value", schedule.encode()).apply()
        val request = PutDataMapRequest.create("/hourly_notice/schedule").apply {
            dataMap.putString("value", schedule.encode())
        }.asPutDataRequest().setUrgent()
        Wearable.getDataClient(this).putDataItem(request)
    }

}

private val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@androidx.compose.runtime.Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleScreen(
    initial: Schedule,
    onSave: (Schedule) -> Unit,
) {
    var schedule by remember { mutableStateOf(initial) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val valid = schedule.isValid()
    val nextNudge = if (valid && schedule.active) {
        AlertTimes.nextAlert(ZonedDateTime.now(ZoneId.systemDefault()), schedule)
            ?.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                androidx.compose.foundation.Image(
                    painter = painterResource(com.hourlynotice.mobile.R.drawable.ic_launcher),
                    contentDescription = "Oisin icon",
                    modifier = Modifier.size(56.dp),
                )
                Column {
                    Text("Oisin", style = MaterialTheme.typography.headlineSmall)
                    Text("Quiet time awareness", color = MaterialTheme.colorScheme.primary)
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(if (schedule.active) "Active" else "Paused", style = MaterialTheme.typography.titleMedium)
                        Text("Alerts are ${if (schedule.active) "enabled" else "muted"}.")
                        if (nextNudge != null) {
                            Text("Next nudge", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(nextNudge, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Switch(checked = schedule.active, onCheckedChange = { schedule = schedule.copy(active = it) })
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Alert window", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimeButton("Starts", schedule.startMinutes, Modifier.weight(1f)) { schedule = schedule.copy(startMinutes = it) }
                        TimeButton("Ends", schedule.endMinutes, Modifier.weight(1f)) { schedule = schedule.copy(endMinutes = it) }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Days", style = MaterialTheme.typography.titleMedium)
                    dayNames.chunked(3).forEach { days ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            days.forEachIndexed { index, name ->
                                val dayIndex = dayNames.indexOf(name)
                                val checked = schedule.daysMask and (1 shl dayIndex) != 0
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(modifier = Modifier.size(36.dp), checked = checked, onCheckedChange = {
                                        val mask = if (it) schedule.daysMask or (1 shl dayIndex) else schedule.daysMask and (1 shl dayIndex).inv()
                                        schedule = schedule.copy(daysMask = mask)
                                    })
                                    Text(name)
                                }
                            }
                            repeat(3 - days.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Alert interval", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 30, 60).forEach { interval ->
                            Button(
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (schedule.intervalMinutes == interval) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (schedule.intervalMinutes == interval) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                                onClick = { schedule = schedule.copy(intervalMinutes = interval) },
                            ) { Text("${interval}m") }
                        }
                    }
                }
            }

            if (!valid) Text("Choose at least one day and make the end time later than the start time.", color = MaterialTheme.colorScheme.error)
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = valid,
                onClick = {
                    onSave(schedule)
                    val next = AlertTimes.nextAlert(ZonedDateTime.now(ZoneId.systemDefault()), schedule)
                    val message = "Saved - next nudge at ${next?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"}"
                    scope.launch { snackbarHostState.showSnackbar(message) }
                },
            ) { Text("Save") }
        }
    }
}

@androidx.compose.runtime.Composable
private fun TimeButton(label: String, minutes: Int, modifier: Modifier = Modifier, onSelected: (Int) -> Unit) {
    val hour = minutes / 60
    val minute = minutes % 60
    val context = androidx.compose.ui.platform.LocalContext.current
    Button(modifier = modifier, onClick = {
        TimePickerDialog(context, com.hourlynotice.mobile.R.style.Theme_HourlyNotice_TimePicker, { _, selectedHour, selectedMinute ->
            onSelected(selectedHour * 60 + selectedMinute)
        }, hour, minute, true).show()
    }) { Text("$label  %02d:%02d".format(hour, minute)) }
}
