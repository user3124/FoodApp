package com.example.foodapp.steps

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.foodapp.R
import com.example.foodapp.notification.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var stepsTracker: StepsTracker

    private lateinit var sensorManager: SensorManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var listening = false
    private var sensorAvailable = true

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SensorManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Обязательно сразу после startForegroundService()
        val type = if (Build.VERSION.SDK_INT >= 34) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        } else {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
        }
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(stepsTracker.todaySteps.value),
            type
        )

        if (!listening) {
            startListening()
            scope.launch {
                stepsTracker.todaySteps.collect { steps -> updateNotification(steps) }
            }
        }
        return START_STICKY
    }

    private fun startListening() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorAvailable = sensor != null
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        listening = true
        updateNotification(stepsTracker.todaySteps.value)
    }

    override fun onSensorChanged(event: SensorEvent) {
        stepsTracker.onSensorTotal(event.values[0].toLong())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun buildNotification(steps: Int): Notification {
        val text = if (sensorAvailable) {
            getString(R.string.steps_notification_text, steps)
        } else {
            getString(R.string.steps_sensor_unavailable)
        }

        // Нажатие на уведомление открывает приложение
        val contentIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return NotificationCompat.Builder(this, NotificationChannels.STEPS)
            .setSmallIcon(R.drawable.ic_stat_steps)
            .setContentTitle(getString(R.string.steps_notification_title))
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateNotification(steps: Int) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(steps))
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1001

        /**
         * Запускает сервис, только если выдано разрешение ACTIVITY_RECOGNITION.
         * Без него датчик шагов недоступен, а на Android 14+ запуск сервиса типа
         * health упадёт с SecurityException. Возвращает false, если запуск не выполнен.
         */
        fun start(context: Context): Boolean {
            val granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false

            ContextCompat.startForegroundService(
                context,
                Intent(context, StepCounterService::class.java)
            )
            return true
        }
    }
}