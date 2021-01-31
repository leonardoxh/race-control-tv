package fr.groggy.racecontrol.tv.utils.http

import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource

private val JSON = "application/json; charset=utf-8".toMediaType()

fun <T> T.toJsonRequestBody(adapter: JsonAdapter<T>): RequestBody =
    adapter.toJson(this).toRequestBody(JSON)

suspend fun <T> Response.parseJsonBody(adapter: JsonAdapter<T>): T =
    if (isSuccessful) body?.source()?.parseJson(adapter)!!
    else throw HttpException(code)

suspend fun <T> BufferedSource.parseJson(adapter: JsonAdapter<T>): T? =
    withContext(Dispatchers.IO) {
        adapter.fromJson(this@parseJson)
    }

class HttpException(val code: Int): IllegalStateException("HTTP request failed with status $code")
