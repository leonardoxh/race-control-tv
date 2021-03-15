package fr.groggy.racecontrol.tv.core.session

import fr.groggy.racecontrol.tv.f1tv.F1TvSession
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionId
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun observe(id: F1TvSessionId): Flow<F1TvSession>

    fun observe(ids: List<F1TvSessionId>): Flow<List<F1TvSession>>

    suspend fun save(session: F1TvSession)

    suspend fun save(sessions: List<F1TvSession>)

}
