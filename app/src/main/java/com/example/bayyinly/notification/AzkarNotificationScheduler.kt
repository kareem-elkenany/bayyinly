package com.example.bayyinly.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AzkarNotificationScheduler {

    // Request codes — unique per alarm, must not clash with prayer (1001/1002)
    const val REQUEST_MORNING   = 2001
    const val REQUEST_EVENING   = 2002
    const val REQUEST_SLEEP     = 2003
    const val REQUEST_RECURRING = 2004

    private const val INTERVAL_RECURRING = 10 * 60 * 1000L // 10 minutes

    // Default daily trigger times  (hour, minute)
    private val DEFAULT_TIMES = mapOf(
        AzkarNotificationReceiver.ACTION_MORNING to Pair(6,  0),
        AzkarNotificationReceiver.ACTION_EVENING to Pair(15, 45),
        AzkarNotificationReceiver.ACTION_SLEEP   to Pair(21, 30)
    )

    private val REQUEST_CODES = mapOf(
        AzkarNotificationReceiver.ACTION_MORNING to REQUEST_MORNING,
        AzkarNotificationReceiver.ACTION_EVENING to REQUEST_EVENING,
        AzkarNotificationReceiver.ACTION_SLEEP   to REQUEST_SLEEP,
        AzkarNotificationReceiver.ACTION_RECURRING to REQUEST_RECURRING
    )

    /** Schedule all daily azkar alarms and the recurring dhikr. */
    fun scheduleAll(context: Context) {
        // Daily Alarms
        DEFAULT_TIMES.forEach { (action, time) ->
            schedule(context, action, time.first, time.second)
        }
        
        // Recurring General Dhikr (every 10 mins)
        rescheduleRecurring(context)
    }

    /** Reschedule a single alarm for the next day at the same time (called after it fires). */
    fun rescheduleForTomorrow(context: Context, action: String) {
        val (hour, minute) = DEFAULT_TIMES[action] ?: return
        val requestCode = REQUEST_CODES[action] ?: return
        val triggerAt = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        setAlarm(context, action, requestCode, triggerAt)
    }

    /** Reschedule the recurring dhikr for 10 minutes from now. */
    fun rescheduleRecurring(context: Context) {
        val triggerAt = System.currentTimeMillis() + INTERVAL_RECURRING
        setAlarm(context, AzkarNotificationReceiver.ACTION_RECURRING, REQUEST_RECURRING, triggerAt)
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        REQUEST_CODES.forEach { (action, requestCode) ->
            val pi = buildPendingIntent(context, action, requestCode,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            pi?.let { alarmManager.cancel(it) }
        }
    }

    private fun schedule(context: Context, action: String, hour: Int, minute: Int) {
        val requestCode = REQUEST_CODES[action] ?: return
        val triggerAt = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time has already passed today, push to tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
        setAlarm(context, action, requestCode, triggerAt)
    }

    private fun setAlarm(context: Context, action: String, requestCode: Int, triggerAt: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(
            context, action, requestCode,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        ) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAt, 10 * 1000L, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun buildPendingIntent(context: Context, action: String, requestCode: Int, flags: Int): PendingIntent? {
        val intent = Intent(context, AzkarNotificationReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}
