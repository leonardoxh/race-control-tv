package fr.groggy.racecontrol.tv.core.session

import android.util.Log
import fr.groggy.racecontrol.tv.core.channel.ChannelService
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import fr.groggy.racecontrol.tv.f1tv.F1TvSeason
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionService @Inject constructor(
    private val repository: SessionRepository,
    private val f1Tv: F1TvClient,
    private val channelService: ChannelService
) {

    companion object {
        private val TAG = SessionService::class.simpleName
    }

    suspend fun loadSessionsWithImages(season: F1TvSeason) {
        Log.d(TAG, "loadSessionsWithImages")

        val sessions = season.events.map { f1Tv.getSessions(it, season) }.flatten()
        repository.save(sessions)
    }

    suspend fun loadChannels(contentId: String) {
        Log.d(TAG, "loadSessionWithImagesAndChannels")
        channelService.loadChannelsWithDrivers(contentId)
    }

}
