package fr.groggy.racecontrol.tv.db.event

import fr.groggy.racecontrol.tv.core.LocalDatePeriod
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.db.IdListMapper
import fr.groggy.racecontrol.tv.db.RaceControlTvDatabase
import fr.groggy.racecontrol.tv.f1tv.F1TvEvent
import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonEvent
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEventRepository @Inject constructor(
    database: RaceControlTvDatabase,
    private val sessionIdListMapper: IdListMapper<F1TvSessionId>
) : EventRepository {

    private val dao = database.eventDao()

    override fun observe(ids: List<F1TvSeasonEvent>): Flow<List<F1TvEvent>> =
        dao.observeById(ids.map { it.id })
            .map { events -> events.map { toEvent(it) } }
            .distinctUntilChanged()

    private fun toEvent(event: EventEntity): F1TvEvent =
        F1TvEvent(
            id = F1TvEventId(event.id),
            name = event.name,
            period = LocalDatePeriod(
                start = LocalDate.ofEpochDay(event.startDate),
                end = LocalDate.ofEpochDay(event.endDate)
            ),
            sessions = sessionIdListMapper.fromDto(event.sessions)
        )

    override suspend fun save(events: List<F1TvEvent>) {
        val entities = events.map { toEntity(it) }
        dao.upsert(entities)
    }

    private fun toEntity(event: F1TvEvent): EventEntity =
        EventEntity(
            id = event.id.value,
            name = event.name,
            meetingKey = "", //TODO FUCK~
            startDate = event.period.start.toEpochDay(),
            endDate = event.period.end.toEpochDay(),
            sessions = sessionIdListMapper.toDto(event.sessions)
        )

}
