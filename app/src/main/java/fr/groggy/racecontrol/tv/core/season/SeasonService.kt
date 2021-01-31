package fr.groggy.racecontrol.tv.core.season

import android.util.Log
import fr.groggy.racecontrol.tv.core.event.EventService
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonId
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonId.Companion.CURRENT
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonService @Inject constructor(
    private val currentSeasonIdRepository: CurrentSeasonIdRepository,
    private val seasonRepository: SeasonRepository,
    private val f1Tv: F1TvClient,
    private val eventService: EventService
) {

    companion object {
        private val TAG = SeasonService::class.simpleName
    }

    suspend fun listArchive(): List<Archive> {
        Log.d(TAG, "listSeasons")
        return f1Tv.listArchive()
    }

    suspend fun loadCurrentSeason() {
        Log.d(TAG, "loadCurrentSeason")
        val season = f1Tv.getSeason(CURRENT)
        seasonRepository.save(season)
        currentSeasonIdRepository.save(season.id)
        eventService.loadEvents(season.events)
    }

    suspend fun loadSeason(id: F1TvSeasonId) {
        Log.d(TAG, "loadSeason")
        val season = f1Tv.getSeason(id)
        seasonRepository.save(season)
        eventService.loadEvents(season.events)
    }

}
