package com.yggdrasil.apitrackerlite.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yggdrasil.apitrackerlite.R
import com.yggdrasil.apitrackerlite.interceptor.ApiInterceptor
import com.yggdrasil.apitrackerlite.interceptor.DataHolder
import com.yggdrasil.apitrackerlite.ui.interceptor.InterceptorListActivity

internal object NotificationUtils {

    private const val CHANNEL_ID = "NOTIFICATION_API_LOG"
    private const val CHANNEL_NAME = "NOTIFICATION_API_LOG_FOR_DEBUG"

    // Extras
    const val TIME_STAMP_EXTRA = "TIME_STAMP_EXTRA"

    private var notificationList: MutableList<Int> = mutableListOf()

    fun create(context: Context, timeStamp: Long) {
        createNotificationChannel(context)

        val notification = createNotificationBuilder(context, createDataWithTimeStamp(timeStamp))

        val notificationId = System.currentTimeMillis().toInt()

        if (notificationList.size >= 10) {
            cancelExpireNotification(context, notificationList[0])
            notificationList.removeAt(0)
        }

        notificationList.add(notificationId)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification.build())
        }
    }

    private fun cancelExpireNotification(context: Context, id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    private fun createDataWithTimeStamp(timeStamp: Long) = DataHolder.getDataByTimeStamp(timeStamp)

    private fun createLatestData() = DataHolder.getLatestData()

    private fun createNotificationBuilder(context: Context, params: ApiInterceptor.Params?) =
        NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSubText(createNotificationTitle(params))
            setContentTitle(createNotificationContentTitle(params))
            setContentText(createNotificationContentText(params))
            setSmallIcon(R.drawable.nil)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(createPendingIntent(context))
        }

    private fun createPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, InterceptorListActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
        )
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "For Debug Api in Non Production Variant."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationTitle(params: ApiInterceptor.Params?): String =
        StringBuilder().apply {
            append(params?.code)
            append(" [${params?.method ?: ""}]")
        }.toString()

    private fun createNotificationContentTitle(params: ApiInterceptor.Params?): String =
        StringBuilder().apply {
            append(" ${(params?.url) ?: ""}")
        }.toString()

    private fun createNotificationContentText(params: ApiInterceptor.Params?): String =
        params?.responseBody ?: ""
}