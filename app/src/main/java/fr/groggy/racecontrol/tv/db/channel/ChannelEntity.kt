package fr.groggy.racecontrol.tv.db.channel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val channelId: String,
    @ColumnInfo(name = "contentId") val contentId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "driver") val driver: String?
)
