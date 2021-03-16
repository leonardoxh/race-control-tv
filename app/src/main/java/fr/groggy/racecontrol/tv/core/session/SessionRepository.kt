package fr.groggy.racecontrol.tv.core.session

import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import fr.groggy.racecontrol.tv.f1tv.F1TvSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun observeById(sessionId: String): Flow<F1TvSession>

    fun observe(ids: List<F1TvEventId>): Flow<List<F1TvSession>>

    suspend fun save(session: F1TvSession)

    suspend fun save(sessions: List<F1TvSession>)

}
