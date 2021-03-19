package fr.groggy.racecontrol.tv.db.channel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "channel_id") val channelId: String?,
    @ColumnInfo(name = "contentId") val contentId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "sub_title") val subTitle: String?,
    @ColumnInfo(name = "background") val background: String?,
    @ColumnInfo(name = "driver") val driver: String?
)
