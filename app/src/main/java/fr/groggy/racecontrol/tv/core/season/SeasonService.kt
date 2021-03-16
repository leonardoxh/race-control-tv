package fr.groggy.racecontrol.tv.core.season

import android.util.Log
import fr.groggy.racecontrol.tv.core.session.SessionService
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import org.threeten.bp.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonService @Inject constructor(
    private val seasonRepository: SeasonRepository,
    private val f1Tv: F1TvClient,
    private val sessionService: SessionService
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

    suspend fun loadSeason(archive: Archive) {
        Log.d(TAG, "loadSeason ${archive.year}")
        val season = f1Tv.getSeason(archive)
        seasonRepository.save(season)
        sessionService.loadSessionsWithImages(season)
    }
}
