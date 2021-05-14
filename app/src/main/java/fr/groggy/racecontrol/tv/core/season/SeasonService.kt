package fr.groggy.racecontrol.tv.core.season

import android.net.Uri
import android.util.Log
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.core.session.SessionService
import fr.groggy.racecontrol.tv.f1tv.*
import fr.groggy.racecontrol.tv.ui.season.browse.Event
import fr.groggy.racecontrol.tv.ui.season.browse.Image
import fr.groggy.racecontrol.tv.ui.season.browse.Season
import fr.groggy.racecontrol.tv.ui.season.browse.Session
import fr.groggy.racecontrol.tv.utils.coroutines.traverse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.threeten.bp.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonService @Inject constructor(
    private val seasonRepository: SeasonRepository,
    private val f1Tv: F1TvClient,
    private val sessionService: SessionService,
    private val eventRepository: EventRepository,
    private val sessionRepository: SessionRepository
) {
    companion object {
        private val TAG = SeasonService::class.simpleName
        private const val F1_ARCHIVE_START_YEAR = 1981
    }

    fun listArchive(): List<Archive> {
        Log.d(TAG, "listSeasons")
        val currentYear = Year.now().value
        val archive = mutableListOf<Archive>()
        for (year in currentYear downTo F1_ARCHIVE_START_YEAR) {
            archive.add(Archive(year))
        }
        return archive
    }

    suspend fun loadSeason(archive: Archive) = withContext(Dispatchers.IO) {
        Log.d(TAG, "loadSeason ${archive.year}")
        val season = f1Tv.getSeason(archive)
        seasonRepository.save(season)
        sessionService.loadSessionsWithImages(season)
    }

    suspend fun season(archive: Archive): Flow<Season> =
        seasonRepository.observe(archive)
            .onEach { Log.d(TAG, "Season changed") }
            .filterNotNull()
            .flatMapLatest { season -> events(season.events)
                .map { events -> Season(
                    name = season.title,
                    events = events
                ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM season changed") }

    private fun events(ids: List<F1TvSeasonEvent>): Flow<List<Event>> =
        eventRepository.observe(ids)
            .onEach { Log.d(TAG, "Events changed") }
            .flatMapLatest { events -> events
                .sortedByDescending { it.period.start }
                .traverse { event -> sessions(listOf(event.id))
                    .map { sessions -> Event(
                        id = event.id,
                        name = event.name,
                        sessions = sessions
                    ) }
                }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM events changed") }

    private fun sessions(ids: List<F1TvEventId>): Flow<List<Session>> =
        sessionRepository.observe(ids)
            .onEach { Log.d(TAG, "Sessions changed") }
            .flatMapLatest { sessions -> sessions
                .filter { it.available && it.channels.isNotEmpty() }
                .sortedByDescending { it.period.start }
                .traverse { session -> thumbnail(session)
                    .map { thumbnail -> Session(
                        id = session.id,
                        contentId = session.contentId,
                        name = session.name,
                        contentSubtype = session.contentSubtype,
                        thumbnail = thumbnail,
                        channels = session.channels
                    ) }
                }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM sessions changed") }

    private fun thumbnail(session: F1TvSession): Flow<Image> {
        return flowOf(
            Image(Uri.parse(session.pictureUrl))
        )
    }
}
