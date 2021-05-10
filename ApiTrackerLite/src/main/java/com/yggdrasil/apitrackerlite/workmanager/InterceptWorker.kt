package com.yggdrasil.apitrackerlite.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yggdrasil.apitrackerlite.notification.NotificationUtils


class InterceptWorker(private val appContext: Context, params: WorkerParameters) :
    Worker(appContext, params) {

    override fun doWork(): Result {
        NotificationUtils.create(appContext, inputData.getLong(NotificationUtils.TIME_STAMP_EXTRA, 0L))

        return Result.success()
    }
}