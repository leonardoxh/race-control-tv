package fr.groggy.racecontrol.tv.ui.season.browse

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.core.season.SeasonRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.f1tv.*
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionStatus.Companion.Live
import fr.groggy.racecontrol.tv.ui.DataClassByIdDiffCallback
import fr.groggy.racecontrol.tv.ui.session.SessionCard
import fr.groggy.racecontrol.tv.utils.coroutines.traverse
import kotlinx.coroutines.flow.*

class SeasonBrowseViewModel @ViewModelInject constructor(
    private val eventRepository: EventRepository,
    private val seasonRepository: SeasonRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        private val TAG = SeasonBrowseViewModel::class.simpleName
    }

    suspend fun archiveLoaded(archive: Archive) {
        loaded(season(archive))
    }

    private suspend fun loaded(season: Flow<Season>) {
        season.filter { it.events.isNotEmpty() }.first()
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
                .filter { it.sessions.isNotEmpty() }
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
                        live = session.status == Live,
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

data class Season(
    val name: String,
    val events: List<Event>
)

data class Event(
    val id: F1TvEventId,
    val name: String,
    val sessions: List<Session>
)

data class Session(
    val id: F1TvSessionId,
    val contentId: String,
    override val name: String,
    override val live: Boolean,
    override val thumbnail: Image?,
    val channels: List<F1TvChannelId>
) : SessionCard {

    companion object {
        val diffCallback = DataClassByIdDiffCallback { session: Session -> session.id }
    }

}

data class Image(
    override val url: Uri
) : SessionCard.Image
