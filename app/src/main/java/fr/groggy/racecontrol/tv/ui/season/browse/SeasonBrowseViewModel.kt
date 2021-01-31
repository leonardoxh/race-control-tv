package fr.groggy.racecontrol.tv.ui.season.browse

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.event.EventRepository
import fr.groggy.racecontrol.tv.core.image.ImageRepository
import fr.groggy.racecontrol.tv.core.season.CurrentSeasonIdRepository
import fr.groggy.racecontrol.tv.core.season.SeasonRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.f1tv.*
import fr.groggy.racecontrol.tv.f1tv.F1TvImageType.Companion.Thumbnail
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionStatus.Companion.Live
import fr.groggy.racecontrol.tv.ui.DataClassByIdDiffCallback
import fr.groggy.racecontrol.tv.ui.session.SessionCard
import fr.groggy.racecontrol.tv.utils.coroutines.traverse
import kotlinx.coroutines.flow.*

class SeasonBrowseViewModel @ViewModelInject constructor(
    private val currentSeasonIdRepository: CurrentSeasonIdRepository,
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository,
    private val seasonRepository: SeasonRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        private val TAG = SeasonBrowseViewModel::class.simpleName
    }

    init {
        Log.d(TAG, "init")
    }

    suspend fun seasonLoaded(id: F1TvSeasonId?) {
        if (id == null) {
            currentSeasonLoaded()
        } else {
            seasonLoadedById(id)
        }
    }

    private suspend fun currentSeasonLoaded() =
        loaded(currentSeason)

    private suspend fun seasonLoadedById(id: F1TvSeasonId) =
        loaded(season(id))

    private suspend fun loaded(season: Flow<Season>) {
        season.filter { it.events.isNotEmpty() }.first()
    }

    val currentSeason: Flow<Season> by lazy {
        currentSeasonIdRepository.observe()
            .onEach { Log.d(TAG, "Current season id changed") }
            .filterNotNull()
            .flatMapLatest { season(it) }
            .onEach { Log.d(TAG, "Current VM season changed") }
    }

    fun season(id: F1TvSeasonId): Flow<Season> =
        seasonRepository.observe(id)
            .onEach { Log.d(TAG, "Season changed") }
            .filterNotNull()
            .flatMapLatest { season -> events(season.events)
                .map { events -> Season(
                    id = season.id,
                    name = season.name,
                    events = events
                ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM season changed") }

    private fun events(ids: List<F1TvEventId>): Flow<List<Event>> =
        eventRepository.observe(ids)
            .onEach { Log.d(TAG, "Events changed") }
            .flatMapLatest { events -> events
                .filter { it.sessions.isNotEmpty() }
                .sortedByDescending { it.period.start }
                .traverse { event -> sessions(event.sessions)
                    .map { sessions -> Event(
                        id = event.id,
                        name = event.name,
                        sessions = sessions
                    ) }
                }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM events changed") }

    private fun sessions(ids: List<F1TvSessionId>): Flow<List<Session>> =
        sessionRepository.observe(ids)
            .onEach { Log.d(TAG, "Sessions changed") }
            .flatMapLatest { sessions -> sessions
                .filter { it.available && it.channels.isNotEmpty() }
                .sortedByDescending { it.period.start }
                .traverse { session -> thumbnail(session.images)
                    .map { thumbnail -> Session(
                        id = session.id,
                        name = session.name,
                        live = session.status == Live,
                        thumbnail = thumbnail,
                        channels = session.channels
                    ) }
                }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM sessions changed") }

    private fun thumbnail(ids: List<F1TvImageId>): Flow<Image?> =
        imageRepository.observe(ids)
            .onEach { Log.d(TAG, "Images changed") }
            .map { images -> images
                .find { it.type == Thumbnail }
                ?.let { Image(
                    id = it.id,
                    url = it.url
                ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM thumbnail changed") }

}

data class Season(
    val id: F1TvSeasonId,
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
    val id: F1TvImageId,
    override val url: Uri
) : SessionCard.Image
