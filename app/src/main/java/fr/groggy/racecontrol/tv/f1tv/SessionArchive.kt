package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionArchive(
    val resultObj: SessionArchiveResultObj
)

@JsonClass(generateAdapter = true)
data class SessionArchiveResultObj(
    val containers: List<SessionArchiveContainer>
)

@JsonClass(generateAdapter = true)
data class SessionArchiveContainer(
    val retrieveItems: SessionArchiveRetrieveItems
)

@JsonClass(generateAdapter = true)
data class SessionArchiveRetrieveItems(
    val resultObj: SessionArchiveRetrieveItemsResultObject
)

@JsonClass(generateAdapter = true)
data class SessionArchiveRetrieveItemsResultObject(
    val containers: List<SessionArchiveRetrieveItemsContainer>?
)

@JsonClass(generateAdapter = true)
data class SessionArchiveRetrieveItemsContainer(
    val id: String,
    val metadata: SessionArchiveRetrieveItemsMetadata
)

@JsonClass(generateAdapter = true)
data class SessionArchiveRetrieveItemsMetadata(
    val contentId: String,
    val pictureUrl: String,
    val title: String,
    val contentSubtype: String,
    val emfAttributes: SessionArchiveRetrieveItemsEmfAttributes
)

@JsonClass(generateAdapter = true)
data class SessionArchiveRetrieveItemsEmfAttributes(
    @Json(name = "MeetingKey") val meetingKey: String
)


