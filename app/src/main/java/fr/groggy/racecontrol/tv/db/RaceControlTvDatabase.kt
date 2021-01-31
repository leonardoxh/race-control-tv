package fr.groggy.racecontrol.tv.db

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.groggy.racecontrol.tv.db.channel.ChannelDao
import fr.groggy.racecontrol.tv.db.channel.ChannelEntity
import fr.groggy.racecontrol.tv.db.driver.DriverDao
import fr.groggy.racecontrol.tv.db.driver.DriverEntity
import fr.groggy.racecontrol.tv.db.event.EventDao
import fr.groggy.racecontrol.tv.db.event.EventEntity
import fr.groggy.racecontrol.tv.db.image.ImageDao
import fr.groggy.racecontrol.tv.db.image.ImageEntity
import fr.groggy.racecontrol.tv.db.season.SeasonDao
import fr.groggy.racecontrol.tv.db.season.SeasonEntity
import fr.groggy.racecontrol.tv.db.session.SessionDao
import fr.groggy.racecontrol.tv.db.session.SessionEntity

@Database(
    entities = [
        ChannelEntity::class,
        DriverEntity::class,
        EventEntity::class,
        ImageEntity::class,
        SeasonEntity::class,
        SessionEntity::class
    ],
    version = 1
)
abstract class RaceControlTvDatabase : RoomDatabase() {

    abstract fun channelDao(): ChannelDao

    abstract fun driverDao(): DriverDao

    abstract fun eventDao(): EventDao

    abstract fun imageDao(): ImageDao

    abstract fun seasonDao(): SeasonDao

    abstract fun sessionDao(): SessionDao

}
