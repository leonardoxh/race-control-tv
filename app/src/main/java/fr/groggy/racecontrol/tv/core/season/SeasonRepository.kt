package fr.groggy.racecontrol.tv.core.season

import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvSeason
import kotlinx.coroutines.flow.Flow

interface SeasonRepository {

    suspend fun observe(archive: Archive): Flow<F1TvSeason?>

    suspend fun save(season: F1TvSeason)

}
