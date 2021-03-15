package fr.groggy.racecontrol.tv.core.event

import android.util.Log
import fr.groggy.racecontrol.tv.core.session.SessionService
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import org.threeten.bp.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventService @Inject constructor(
    private val repository: EventRepository,
    private val f1Tv: F1TvClient,
    private val sessionService: SessionService,
    private val clock: Clock
) {

    companion object {
        private val TAG = EventService::class.simpleName
    }

    suspend fun loadEvents(ids: List<F1TvEventId>) {
        Log.d(TAG, "loadEvents")

        //TODO this logic might be reused
//        val (future, pastAndCurrent) = events.partition { it.isFutureEvent(clock) }
//        (pastAndCurrent.sortedByDescending { it.period.start } + future)
//            .forEach {
//                try {
//                    sessionService.loadSessionsWithImages(it.sessions)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
    }

}
