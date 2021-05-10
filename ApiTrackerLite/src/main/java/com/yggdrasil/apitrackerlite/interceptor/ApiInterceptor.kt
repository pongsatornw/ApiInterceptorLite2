package com.yggdrasil.apitrackerlite.interceptor

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yggdrasil.apitrackerlite.notification.NotificationUtils
import com.yggdrasil.apitrackerlite.workmanager.InterceptWorker
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.*


typealias OnTimeout = () -> Unit

// Pass application context here.
class ApiInterceptor(private var context: Context?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().build()
        val response = chain.proceed(chain.request())

        interceptResponse(response)

        return chain.proceed(request.newBuilder().build())
    }

    private fun interceptResponse(response: Response) {
        val source = response.body?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer
        val responseBody = buffer?.clone()?.readString(Charsets.UTF_8)

        val requestBuffer = Buffer()
        response.request.body?.writeTo(requestBuffer)
        val requestBody = requestBuffer.readUtf8()

        context?.let { context ->
            val timeStamp = System.currentTimeMillis()

            DataHolder.addData(
                Params(
                    url = response.request.url.toUri().toString(),
                    method = response.request.method,
                    headers = response.headers,
                    code = response.code,
                    message = response.message,
                    requestBody = requestBody,
                    responseBody = responseBody,
                    timeStamp = timeStamp,
                    requestTime = response.sentRequestAtMillis,
                    responseTime = response.receivedResponseAtMillis
                )
            )

            createWorkRequest(context, timeStamp)
        }
    }

    private fun createWorkRequest(context: Context, timeStamp: Long) {
        val uploadWorker = OneTimeWorkRequestBuilder<InterceptWorker>().apply {
            val data = Data.Builder()
                .putLong(NotificationUtils.TIME_STAMP_EXTRA, timeStamp)
            setInputData(data.build())
        }.build()

        WorkManager.getInstance(context).enqueue(uploadWorker)
    }

    data class Params(
        val url: String,
        val method: String,
        val headers: Headers,
        val code: Int,
        val message: String?,
        val requestBody: String?,
        val responseBody: String?,
        val timeStamp: Long,
        val requestTime: Long,
        val responseTime: Long,
        var onTimeout: OnTimeout = {},
        private val countDownTime: Long = (600 * 1_000)
    ) {

        init {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    onTimeout.invoke()
                }
            }, countDownTime, countDownTime)

        }

        fun createCodeAndMethod() = StringBuilder().apply {
            append(code)
            append(" [$method]")
        }.toString()
    }
}