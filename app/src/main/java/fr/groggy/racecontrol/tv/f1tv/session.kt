package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.groggy.racecontrol.tv.core.InstantPeriod

@JsonClass(generateAdapter = true)
data class F1TvSessionResponse(
    val resultObj: F1TvSessionResult
)

@JsonClass(generateAdapter = true)
data class F1TvSessionResult(
    val containers: List<F1TvSessionResultContainer>
)

@JsonClass(generateAdapter = true)
data class F1TvSessionResultContainer(
    val id: String,
    val metadata: F1TvSessionMetadata
)

@JsonClass(generateAdapter = true)
data class F1TvSessionMetadata(
    val emfAttributes: F1TvSessionEmfAttributes,
    val title: String,
    val pictureUrl: String?,
    val contentSubtype: String,
    val contentId: String
)

@JsonClass(generateAdapter = true)
data class F1TvSessionEmfAttributes(
    @Json(name = "MeetingKey") val meetingKey: String,
    @Json(name = "Meeting_Start_Date") val startDate: String,
    @Json(name = "Meeting_End_Date") val endDate: String
)

inline class F1TvSessionId(val value: String)

data class F1TvSession(
    val id: F1TvSessionId,
    val name: String,
    val eventId: String,
    val pictureUrl: String,
    val contentId: String,
    val contentSubtype: String,
    val period: InstantPeriod,
    val available: Boolean,
    val images: List<F1TvImageId>,
    val channels: List<F1TvChannelId>
)
