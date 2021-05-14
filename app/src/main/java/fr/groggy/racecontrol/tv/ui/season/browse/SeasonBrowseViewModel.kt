package fr.groggy.racecontrol.tv.ui.season.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvChannelId
import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionId
import fr.groggy.racecontrol.tv.ui.DataClassByIdDiffCallback
import fr.groggy.racecontrol.tv.ui.session.SessionCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.threeten.bp.Year
import javax.inject.Inject

@HiltViewModel
class SeasonBrowseViewModel @Inject constructor(
    private val seasonService: SeasonService
) : ViewModel() {

    suspend fun archiveLoaded(archive: Archive): Season {
        return loaded(seasonService.season(archive))
    }

    suspend fun getSeason(archive: Archive): Flow<Season> {
        return seasonService.season(archive)
    }

    private suspend fun loaded(season: Flow<Season>): Season {
        return season.filter { it.events.isNotEmpty() }.first()
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
    override val contentSubtype: String,
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
