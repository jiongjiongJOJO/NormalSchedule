package com.liflymark.normalschedule.logic.network

import com.liflymark.normalschedule.logic.network.NormalScheduleNetwork.getId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NormalScheduleNetwork {
    private val CourseService = ServiceCreator.create(CourseService::class.java)

    suspend fun getId() = CourseService.getId().await()

    suspend fun getCaptcha(sessionID: String) = CourseService.getCaptcha(sessionID).await()

    suspend fun getCourse(user:String,password:String, yzm:String, headers:String) =
        CourseService.getCourse(user, password, yzm, headers).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("Response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}