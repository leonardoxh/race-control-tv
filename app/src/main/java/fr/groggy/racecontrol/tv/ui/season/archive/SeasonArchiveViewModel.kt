package fr.groggy.racecontrol.tv.ui.season.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive

class SeasonArchiveViewModel @ViewModelInject constructor(
    private val seasonService: SeasonService
): ViewModel() {
    fun listArchive(): List<Archive> {
        return seasonService.listArchive()
    }
}
