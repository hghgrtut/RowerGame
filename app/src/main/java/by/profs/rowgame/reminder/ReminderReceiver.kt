package by.profs.rowgame.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.trainings),
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = context.getString(R.string.train_reminder)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.logo_app)
            .setContentTitle("Тренировка")
            .setContentText("Don't forget about it")
            .build()
        notificationManager.notify(NOTIFICATION_TRAINING_ID, notification)
        setNotification(context)
    }

    companion object {
        private const val NOTIFICATION_TRAINING_REQUEST = 123
        private const val NOTIFICATION_TRAINING_ID = 189787
        private const val NOTIFICATION_CHANNEL_ID = "T"

        fun setNotification(context: Context) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() -
                        (System.currentTimeMillis() % DateUtils.DAY_IN_MILLIS) +
                        DateUtils.DAY_IN_MILLIS +
                        Calendar(context.applicationContext).getTrainingTime(),
                PendingIntent.getBroadcast(context,
                    NOTIFICATION_TRAINING_REQUEST,
                    Intent(context, ReminderReceiver::class.java),
                    PendingIntent.FLAG_CANCEL_CURRENT)
            )
        }
    }
}