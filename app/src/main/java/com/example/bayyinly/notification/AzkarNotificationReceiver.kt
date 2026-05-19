package com.example.bayyinly.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.bayyinly.R
import com.example.bayyinly.database.AzkarDatabase
import com.example.bayyinly.model.entity.AzkarEntry
import com.example.bayyinly.repository.AzkarRepository
import com.example.bayyinly.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AzkarNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MORNING   = "com.example.bayyinly.AZKAR_MORNING"
        const val ACTION_EVENING   = "com.example.bayyinly.AZKAR_EVENING"
        const val ACTION_SLEEP     = "com.example.bayyinly.AZKAR_SLEEP"
        const val ACTION_RECURRING = "com.example.bayyinly.AZKAR_RECURRING"

        private const val CHANNEL_ID      = "azkar_reminders"
        private const val NOTIF_MORNING   = 2001
        private const val NOTIF_EVENING   = 2002
        private const val NOTIF_SLEEP     = 2003
        private const val NOTIF_RECURRING = 2004
    }

    override fun onReceive(context: Context, intent: Intent) {
        ensureChannel(context)
        val pendingResult = goAsync()
        val repo = AzkarRepository(AzkarDatabase.getDatabase(context).azkarDao())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_MORNING -> {
                        val entries = repo.getEntriesByCategorySync(AzkarRepository.MORNING)
                        showNotification(context, "Morning Azkar", entries.minByOrNull { it.zekr.length }, NOTIF_MORNING)
                        AzkarNotificationScheduler.rescheduleForTomorrow(context, ACTION_MORNING)
                    }
                    ACTION_EVENING -> {
                        val entries = repo.getEntriesByCategorySync(AzkarRepository.EVENING)
                        showNotification(context, "Evening Azkar", entries.minByOrNull { it.zekr.length }, NOTIF_EVENING)
                        AzkarNotificationScheduler.rescheduleForTomorrow(context, ACTION_EVENING)
                    }
                    ACTION_SLEEP -> {
                        val entries = repo.getEntriesByCategorySync(AzkarRepository.SLEEP)
                        showNotification(context, "Bedtime Azkar", entries.minByOrNull { it.zekr.length }, NOTIF_SLEEP)
                        AzkarNotificationScheduler.rescheduleForTomorrow(context, ACTION_SLEEP)
                    }
                    ACTION_RECURRING -> {
                        val entry = repo.getRandomEntry()
                        showNotification(context, "Dhikr Reminder", entry, NOTIF_RECURRING)
                        AzkarNotificationScheduler.rescheduleRecurring(context)
                    }
                    Intent.ACTION_BOOT_COMPLETED -> {
                        AzkarNotificationScheduler.scheduleAll(context)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, categoryTitle: String, entry: AzkarEntry?, notifId: Int) {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_azkar).apply {
            setTextViewText(R.id.tvAzkarNotifCategory, categoryTitle)
            setTextViewText(R.id.tvAzkarNotifArabic, entry?.zekr ?: "")
            setTextViewText(R.id.tvAzkarNotifTranslation, entry?.description ?: "")
        }

        val tapIntent = PendingIntent.getActivity(
            context, notifId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tasbih)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notifId, notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel(CHANNEL_ID) != null) return
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Azkar Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders and periodic general dhikr"
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }
}
