package fr.groggy.racecontrol.tv.ui.season.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive
import java.time.Year

class SeasonArchiveViewModel @ViewModelInject constructor(
    private val seasonService: SeasonService
): ViewModel() {
    suspend fun listArchive(): List<Archive> {
        val currentYear = Year.now().value
        return listOf(currentArchive(currentYear)) +
            seasonService.listArchive(currentYear).filter { it.hasContent }
    }

    private fun currentArchive(year: Int) = Archive(
        uid = "current",
        year = year,
        hasContent = true
    )
}
