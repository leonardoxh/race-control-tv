package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.groggy.racecontrol.tv.core.InstantPeriod
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
    val metadata: F1TvSeasonMetadata,
    val actions: List<F1TvSeasonAction>
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonAction(
    val targetType: String,
    val uri: String
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonMetadata(
    val emfAttributes: F1TvSeasonEmfAttributes
)

@JsonClass(generateAdapter = true)
data class F1TvSeasonEmfAttributes(
    @Json(name = "MeetingKey") val meetingKey: String,
    @Json(name = "Global_Meeting_Name") val title: String,
    @Json(name = "Meeting_Start_Date") val startDate: String?,
    @Json(name = "Meeting_End_Date") val endDate: String?
)

data class F1TvSeason(
    val year: Year,
    val title: String,
    val events: List<F1TvSeasonEvent>,
    val detailAction: String?
)

data class F1TvSeasonEvent(
    val id: String,
    val meetingKey: String,
    val title: String,
    val period: InstantPeriod
)
