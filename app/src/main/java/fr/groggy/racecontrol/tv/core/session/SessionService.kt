package fr.groggy.racecontrol.tv.core.session

import android.util.Log
import fr.groggy.racecontrol.tv.core.channel.ChannelService
import fr.groggy.racecontrol.tv.core.image.ImageService
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import fr.groggy.racecontrol.tv.f1tv.F1TvSeason
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionId
import fr.groggy.racecontrol.tv.utils.coroutines.concurrentMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionService @Inject constructor(
    private val repository: SessionRepository,
    private val f1Tv: F1TvClient,
    private val channelService: ChannelService,
    private val imageService: ImageService
) {

    companion object {
        private val TAG = SessionService::class.simpleName
    }

    suspend fun loadSessionsWithImages(season: F1TvSeason) {
        Log.d(TAG, "loadSessionsWithImages")

        val sessions = season.events.map { f1Tv.getSessions(it) }.flatten()
        repository.save(sessions)
        val (available, unavailable) = sessions.partition { it.available }
        (available.sortedByDescending { it.period.start } + unavailable)
            .forEach { imageService.loadImages(it.images) }
    }

    suspend fun loadSessionWithImagesAndChannels(id: F1TvSessionId) {
        Log.d(TAG, "loadSessionWithImagesAndChannels")
//        val session = f1Tv.getSession(id)
//        repository.save(session)
//        imageService.loadImages(session.images)
//        channelService.loadChannelsWithDrivers(session.channels)
    }

}
