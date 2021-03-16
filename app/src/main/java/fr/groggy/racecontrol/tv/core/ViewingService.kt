package fr.groggy.racecontrol.tv.core

import android.util.Log
import fr.groggy.racecontrol.tv.core.token.TokenService
import fr.groggy.racecontrol.tv.f1.F1Client
import fr.groggy.racecontrol.tv.f1tv.F1TvViewing
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewingService @Inject constructor(
    private val f1Tv: F1Client,
    private val tokenService: TokenService
) {

    companion object {
        private val TAG = ViewingService::class.simpleName
    }

    suspend fun getViewing(channelId: String?, contentId: String): F1TvViewing {
        Log.d(TAG, "getViewing $channelId - $contentId")
        val token = tokenService.loadAndGetF1Token()
        //Small work around until I can find a good solution for it
        return f1Tv.getViewing(if (channelId == contentId) null else channelId, contentId, token.value)
    }

}
