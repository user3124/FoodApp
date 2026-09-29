package com.example.foodapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.foodapp.R

object NotificationChannels {
    const val STEPS = "steps_channel"
    const val REMINDERS = "reminders_channel"

    // minSdk = 26, поэтому каналы доступны всегда
    fun createAll(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val steps = NotificationChannel(
            STEPS,
            context.getString(R.string.channel_steps_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.channel_steps_description)
            setShowBadge(false)
        }

        val reminders = NotificationChannel(
            REMINDERS,
            context.getString(R.string.channel_reminders_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_reminders_description)
        }

        manager.createNotificationChannels(listOf(steps, reminders))
    }
}