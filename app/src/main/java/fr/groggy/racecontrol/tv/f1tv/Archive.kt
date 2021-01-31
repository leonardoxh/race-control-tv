package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Archive(
    @Json(name = "uid") val uid: String,
    @Json(name = "year") val year: Int,
    @Json(name = "has_content") val hasContent: Boolean,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class ArchiveResponse(
    @Json(name = "objects") val objects: List<Archive>
)
