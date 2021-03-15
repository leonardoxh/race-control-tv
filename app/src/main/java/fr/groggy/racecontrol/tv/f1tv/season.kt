package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.threeten.bp.Year

@JsonClass(generateAdapter = true)
data class F1TvSeasonResponse(
    val resultObj: F1TvSeasonResult
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonResult(
    val containers: List<F1TvSeasonResultContainer>
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonResultContainer(
    val id: String,
    val metadata: F1TvSeasonMetadata
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonMetadata(
    val emfAttributes: F1TvSeasonEmfAttributes,
    val title: String
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonEmfAttributes(
    @Json(name = "MeetingKey") val meetingKey: String
)


inline class F1TvSeasonId(val value: String)

data class F1TvSeason(
    val year: Year,
    val title: String,
    val events: List<F1TvSeasonEvent>
)

data class F1TvSeasonEvent(
    val id: String,
    val meetingKey: String,
    val title: String
)
