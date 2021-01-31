package fr.groggy.racecontrol.tv.ui.season.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService

class SeasonArchiveViewModel @ViewModelInject constructor(
    private val seasonService: SeasonService
): ViewModel() {
    suspend fun listArchive() {
        val archive = seasonService.listArchive()
    }
}
