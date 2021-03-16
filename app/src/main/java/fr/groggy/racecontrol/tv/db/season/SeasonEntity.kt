package fr.groggy.racecontrol.tv.db.season

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasons")
data class SeasonEntity(
    @PrimaryKey @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "detail_action") val detailAction: String?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "events") val events: String
)
