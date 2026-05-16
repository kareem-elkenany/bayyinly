package com.example.bayyinly.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.bayyinly.R
import com.example.bayyinly.model.Timings
import com.example.bayyinly.repository.PrayerRepository
import com.example.bayyinly.ui.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class PrayerNotificationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timer: CountDownTimer? = null
    private lateinit var prayerRepository: PrayerRepository
    
    private val CHANNEL_ID = "prayer_notifications"
    private val ALERT_CHANNEL_ID = "prayer_alerts"
    private val NOTIFICATION_ID = 1001
    private val ALERT_NOTIFICATION_ID = 1002

    private var lastLat: Double = 30.0444
    private var lastLng: Double = 31.2357

    override fun onCreate() {
        super.onCreate()
        val prayerApi = RetrofitClient.prayerApiService
        prayerRepository = PrayerRepository(prayerApi)
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val lat = intent?.getDoubleExtra("lat", 30.0444) ?: 30.0444
        val lng = intent?.getDoubleExtra("lng", 31.2357) ?: 31.2357
        lastLat = lat
        lastLng = lng

        // Start foreground immediately with a placeholder
        startForeground(NOTIFICATION_ID, createPlaceholderNotification())

        fetchAndStartNotification(lat, lng)

        return START_STICKY
    }

    private fun createPlaceholderNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle("Bayyinly Prayer Times")
            .setContentText("Calculating next prayer...")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun fetchAndStartNotification(lat: Double, lng: Double) {
        serviceScope.launch {
            try {
                val response = prayerRepository.getPrayerTimesByCoords(lat, lng)
                if (response.isSuccessful && response.body() != null) {
                    val timings = response.body()!!.data.timings
                    processNextPrayer(timings)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processNextPrayer(timings: Timings) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        val currentTimeString = sdf.format(now.time)
        val currentTime = sdf.parse(currentTimeString)

        val prayerList = listOf(
            "Fajr" to timings.fajr,
            "Dhuhr" to timings.dhuhr,
            "Asr" to timings.asr,
            "Maghrib" to timings.maghrib,
            "Isha" to timings.isha
        )

        var nextName = "Fajr"
        var nextTimeStr = timings.fajr
        var isTomorrow = true

        for ((name, time) in prayerList) {
            val prayerTime = sdf.parse(time.split(" ")[0])
            if (prayerTime != null && prayerTime.after(currentTime)) {
                nextName = name
                nextTimeStr = time
                isTomorrow = false
                break
            }
        }

        startCountdown(nextName, nextTimeStr, isTomorrow)
    }

    private fun startCountdown(name: String, timeStr: String, isTomorrow: Boolean) {
        timer?.cancel()

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val targetDate = sdf.parse(timeStr.split(" ")[0]) ?: return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            time = targetDate
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            if (isTomorrow) add(Calendar.DAY_OF_MONTH, 1)
        }

        val diff = target.timeInMillis - now.timeInMillis

        timer = object : CountDownTimer(diff, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = (millisUntilFinished / 3600000)
                val m = (millisUntilFinished / 60000) % 60
                val s = (millisUntilFinished / 1000) % 60
                val countdown = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
                updateNotification(name, formatTo12Hour(timeStr), countdown)
            }

            override fun onFinish() {
                triggerPrayerAlert(name)
                fetchAndStartNotification(lastLat, lastLng)
            }
        }.start()
    }

    private fun triggerPrayerAlert(prayerName: String) {
        // 1. Vibrate
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }

        // 2. Show Heads-up Notification (Pop-up)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alertNotification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle("Prayer Time")
            .setContentText("It's time for $prayerName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ALERT_NOTIFICATION_ID, alertNotification)
    }

    private fun updateNotification(name: String, time: String, countdown: String) {
        val remoteViews = RemoteViews(packageName, R.layout.notification_prayer)
        remoteViews.setTextViewText(R.id.tvNotificationPrayerName, name)
        remoteViews.setTextViewText(R.id.tvNotificationPrayerTime, time)
        remoteViews.setTextViewText(R.id.tvNotificationCountdown, "- $countdown")

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTo12Hour(time24: String): String {
        return try {
            val cleanTime = time24.split(" ")[0]
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val sdf12 = SimpleDateFormat("h:mm a", Locale.getDefault())
            val date = sdf24.parse(cleanTime)
            sdf12.format(date!!)
        } catch (e: Exception) {
            time24
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Background Channel (Silent)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Times Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(serviceChannel)

            // Alert Channel (Heads-up)
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Prayer Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pop-up notifications when it's time to pray"
                enableVibration(true)
            }
            manager.createNotificationChannel(alertChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        serviceScope.cancel()
    }
}