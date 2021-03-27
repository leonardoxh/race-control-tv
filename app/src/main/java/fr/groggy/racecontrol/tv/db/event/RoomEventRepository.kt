package fr.groggy.racecontrol.tv.db.event

import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.db.RaceControlTvDatabase
import fr.groggy.racecontrol.tv.f1tv.F1TvEvent
import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEventRepository @Inject constructor(
    database: RaceControlTvDatabase
) : EventRepository {

    private val dao = database.eventDao()

    override fun observe(ids: List<F1TvSeasonEvent>): Flow<List<F1TvEvent>> =
        dao.observeById(ids.map { it.id })
            .map { events -> events.map { toEvent(it) } }
            .distinctUntilChanged()

    private fun toEvent(event: EventEntity): F1TvEvent {
        return F1TvEvent(
            id = F1TvEventId(event.id),
            name = event.name,
            period = InstantPeriod(
                start = Instant.ofEpochMilli(event.startDate),
                end = Instant.ofEpochMilli(event.endDate)
            )
        )
    }
}
