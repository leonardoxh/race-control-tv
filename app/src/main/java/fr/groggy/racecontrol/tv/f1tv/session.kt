package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.groggy.racecontrol.tv.core.InstantPeriod
import java.util.*

@JsonClass(generateAdapter = true)
data class F1TVSessionAvailabilityDetailsResponse(
    @Json(name = "is_available") val isAvailable: Boolean
)

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

sealed class F1TvSessionStatus {
    companion object {
        object Replay : F1TvSessionStatus()
        object Live : F1TvSessionStatus()
        object Upcoming : F1TvSessionStatus()
        data class Unknown(val value: String) : F1TvSessionStatus()

        fun from(value: String): F1TvSessionStatus =
            when (value.toLowerCase(Locale.ROOT)) {
                "replay" -> Replay
                "live" -> Live
                "upcoming" -> Upcoming
                else -> Unknown(value)
            }
    }
}

data class F1TvSession(
    val id: F1TvSessionId,
    val name: String,
    val eventId: String,
    val pictureUrl: String,
    val contentId: String,
    val status: F1TvSessionStatus,
    val period: InstantPeriod,
    val available: Boolean,
    val images: List<F1TvImageId>,
    val channels: List<F1TvChannelId>
)
