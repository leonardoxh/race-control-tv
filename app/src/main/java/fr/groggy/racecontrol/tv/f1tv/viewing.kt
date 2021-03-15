package fr.groggy.racecontrol.tv.f1tv

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class F1TvViewingRequest(
    @Json(name = "channel_url") val channelUrl: String
)

@JsonClass(generateAdapter = true)
data class F1TvViewingResponse(
    val resultObj: F1TvViewingResponseResultObject
)

@JsonClass(generateAdapter = true)
data class F1TvViewingResponseResultObject(
    val url: String
)

data class F1TvViewing(
    val url: Uri
)
