package fr.groggy.racecontrol.tv.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seasonService: SeasonService
): ViewModel() {
    fun listArchive(): List<Archive> {
        return seasonService.listArchive()
    }
}
