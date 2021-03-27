package fr.groggy.racecontrol.tv.db.season

import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.core.season.SeasonRepository
import fr.groggy.racecontrol.tv.db.RaceControlTvDatabase
import fr.groggy.racecontrol.tv.db.event.EventEntity
import fr.groggy.racecontrol.tv.f1tv.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import org.threeten.bp.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSeasonRepository @Inject constructor(
    private val database: RaceControlTvDatabase
) : SeasonRepository {

    private val dao = database.seasonDao()

    override suspend fun observe(archive: Archive): Flow<F1TvSeason?> =
        dao.observeByYear(archive.year)
            .map { season -> season?.let { toSeason(it) } }
            .distinctUntilChanged()

    private suspend fun toSeason(season: SeasonEntity): F1TvSeason {
        val seasonEvents = mutableListOf<F1TvSeasonEvent>()
        season.events.split(',').forEach { eventId ->
            seasonEvents += database.eventDao().findEventsById(eventId).map {
                F1TvSeasonEvent(
                    id = it.id,
                    meetingKey = it.meetingKey,
                    title = it.name,
                    period = InstantPeriod(
                        start = Instant.ofEpochMilli(it.startDate),
                        end = Instant.ofEpochMilli(it.endDate)
                    )
                )
            }
        }
        return F1TvSeason(
            year = Year.of(season.year),
            title = season.name,
            detailAction = season.detailAction,
            events = seasonEvents
        )
    }

    override suspend fun save(season: F1TvSeason) {
        val entity = toEntity(season)
        dao.upsert(entity)

        val entities = season.events.map {
            EventEntity(
                id = it.id,
                meetingKey = it.meetingKey,
                name = it.title,
                startDate = it.period.start.toEpochMilli(),
                endDate = it.period.end.toEpochMilli()
            )
        }
        database.eventDao().upsert(entities)
    }

    private fun toEntity(season: F1TvSeason): SeasonEntity =
        SeasonEntity(
            year = season.year.value,
            detailAction = season.detailAction,
            name = season.title,
            events = season.events.joinToString(separator = ",", transform = { it.id })
        )
}
