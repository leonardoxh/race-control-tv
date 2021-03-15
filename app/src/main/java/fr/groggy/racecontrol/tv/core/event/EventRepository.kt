package fr.groggy.racecontrol.tv.core.event

import fr.groggy.racecontrol.tv.f1tv.F1TvEvent
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonEvent
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun observe(ids: List<F1TvSeasonEvent>): Flow<List<F1TvEvent>>

    suspend fun save(events: List<F1TvEvent>)

}
