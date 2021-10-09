package com.example.pecodetesttask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationUtils {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.basic_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH
                )
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context, fragmentIndex: Int, notificationId: Int) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(MainActivity.KEY_FRAGMENT_INDEX, fragmentIndex)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                fragmentIndex,
                intent,
                0
            )

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.small_notification_icon)
                .setContentTitle(context.getString(R.string.chat_heads_active))
                .setContentText(
                    context.getString(
                        R.string.notification_text_placeholder,
                        fragmentIndex + 1
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }

        fun deleteNotification(context: Context, notificationId: Int) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }
    }
}