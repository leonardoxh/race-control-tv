package fr.groggy.racecontrol.tv.db.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "event_id") val eventId: String,
    @ColumnInfo(name = "picture_url") val pictureUrl: String,
    @ColumnInfo(name = "content_id") val contentId: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long,
    @ColumnInfo(name = "available") val available: Boolean,
    @ColumnInfo(name = "images") val images: String,
    @ColumnInfo(name = "channels") val channels: String
)
