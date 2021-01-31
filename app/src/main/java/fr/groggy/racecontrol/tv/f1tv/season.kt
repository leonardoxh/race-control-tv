package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Year

@JsonClass(generateAdapter = true)
data class F1TvSeasonResponse(
    val self: String,
    val name: String,
    val year: Int,
    @Json(name = "eventoccurrence_urls") val eventOccurrenceUrls: List<String>
)

inline class F1TvSeasonId(val value: String) {
    companion object {
        // 2020 example = race_11cd5edd03404b6199bf4712efb34391
        val CURRENT = F1TvSeasonId("/api/race-season/current/")

        fun ofUid(uid: String) = F1TvSeasonId("/api/race-season/$uid/")
    }
}

data class F1TvSeason(
    val id: F1TvSeasonId,
    val name: String,
    val year: Year,
    val events: List<F1TvEventId>
)
