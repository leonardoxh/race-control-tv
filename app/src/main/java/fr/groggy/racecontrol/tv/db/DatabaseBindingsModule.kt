package fr.groggy.racecontrol.tv.db

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.groggy.racecontrol.tv.core.channel.ChannelRepository
import fr.groggy.racecontrol.tv.core.driver.DriverRepository
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.core.image.ImageRepository
import fr.groggy.racecontrol.tv.core.season.SeasonRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.db.channel.RoomChannelRepository
import fr.groggy.racecontrol.tv.db.driver.RoomDriverRepository
import fr.groggy.racecontrol.tv.db.event.RoomEventRepository
import fr.groggy.racecontrol.tv.db.image.RoomImageRepository
import fr.groggy.racecontrol.tv.db.season.RoomSeasonRepository
import fr.groggy.racecontrol.tv.db.session.RoomSessionRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindingsModule {

    @Binds
    abstract fun channelRepository(repository: RoomChannelRepository): ChannelRepository

    @Binds
    abstract fun driverRepository(repository: RoomDriverRepository): DriverRepository

    @Binds
    abstract fun eventRepository(repository: RoomEventRepository): EventRepository

    @Binds
    abstract fun imageRepository(repository: RoomImageRepository): ImageRepository

    @Binds
    abstract fun seasonRepository(repository: RoomSeasonRepository): SeasonRepository

    @Binds
    abstract fun sessionRepository(repository: RoomSessionRepository): SessionRepository

}
