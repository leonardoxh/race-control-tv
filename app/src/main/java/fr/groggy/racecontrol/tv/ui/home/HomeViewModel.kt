package fr.groggy.racecontrol.tv.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.season.browse.Season
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seasonService: SeasonService
): ViewModel() {

    fun listArchive(): List<Archive> {
        return seasonService.listArchive()
    }

    suspend fun getCurrentSeason(archive: Archive): Flow<Season> {
        return seasonService.season(archive)
    }
}
