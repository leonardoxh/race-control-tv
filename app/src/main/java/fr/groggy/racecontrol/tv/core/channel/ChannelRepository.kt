package fr.groggy.racecontrol.tv.core.channel

import fr.groggy.racecontrol.tv.f1tv.F1TvChannel
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {

    fun observe(contentId: String): Flow<List<F1TvChannel>>

    suspend fun save(channels: List<F1TvChannel>)

}
