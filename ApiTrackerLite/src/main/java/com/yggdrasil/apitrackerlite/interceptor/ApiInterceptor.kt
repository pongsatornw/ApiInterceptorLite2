package com.yggdrasil.apitrackerlite.interceptor

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yggdrasil.apitrackerlite.notification.NotificationUtils
import com.yggdrasil.apitrackerlite.workmanager.InterceptWorker
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import kotlin.math.min


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

        responseBody?.let {
            when (val json = JSONTokener(it).nextValue()) {
                is JSONObject -> interceptJsonObject(json)
                is JSONArray -> interceptJsonArray(json)
            }
        }

        response.receivedResponseAtMillis

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

    private fun interceptJsonObject(obj: JSONObject) {
        obj.keys().forEach { key ->
            when (obj[key]) {
                is JSONObject -> interceptJsonObject(obj[key] as JSONObject)
                is JSONArray -> interceptJsonArray(obj[key] as JSONArray)
                else -> interceptValue(key, obj[key])
            }
        }
    }

    private fun interceptJsonArray(array: JSONArray) {
        for (i in 0 until array.length()) {
            interceptJsonObject(array[i] as JSONObject)
            when (array[i]) {
                is JSONObject -> interceptJsonObject(array[i] as JSONObject)
                is JSONArray -> interceptJsonArray(array[i] as JSONArray)
                else -> interceptValue(array[i])
            }
        }
    }

    private fun interceptValue(value: Any) {
        val type: String? = when (value::class.java) {
            value::class.javaPrimitiveType -> value::class.javaPrimitiveType?.toString()
            value::class.javaObjectType -> value::class.javaObjectType.toString()
            else -> "UNKNOWN"
        }
        Log.i(type, value.toString())
    }

    private fun getValueType(value: Any): String {
        return when (value::class.java) {
            value::class.javaPrimitiveType -> value::class.javaPrimitiveType?.simpleName ?: "Null"
            value::class.javaObjectType -> value::class.javaObjectType.simpleName
            else -> "UNKNOWN"
        }
    }

    private fun interceptValue(key: String, value: Any) {
        Log.i("$key [${getValueType(value)}]", value.toString().also {
            it.substring(0, min(30, it.length))
        })
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