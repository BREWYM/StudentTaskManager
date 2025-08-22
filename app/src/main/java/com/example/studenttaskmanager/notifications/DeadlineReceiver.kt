package com.example.studenttaskmanager.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.studenttaskmanager.R
import kotlin.random.Random

class DeadlineReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Напоминание"
        val deadline = intent.getStringExtra("deadline") ?: ""

        val builder = NotificationCompat.Builder(context, "deadline_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Скоро дедлайн!")
            .setContentText("$title — до $deadline")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // Проверка разрешения для API 33+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(Random.nextInt(), builder.build())
        } else {
            Log.w("DeadlineReceiver", "Нет разрешения на уведомления (POST_NOTIFICATIONS)")
        }
    }
}