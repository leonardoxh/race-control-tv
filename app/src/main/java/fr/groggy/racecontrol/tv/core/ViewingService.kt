package fr.groggy.racecontrol.tv.core

import android.util.Log
import fr.groggy.racecontrol.tv.core.token.TokenService
import fr.groggy.racecontrol.tv.f1.F1Client
import fr.groggy.racecontrol.tv.f1tv.F1TvViewing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewingService @Inject constructor(
    private val f1Tv: F1Client,
    private val tokenService: TokenService
) {
    companion object {
        private val TAG = ViewingService::class.simpleName
        private const val RETRY_DELAY_IN_MILLS = 5_000L
        private const val MAX_RETRIES = 5
    }

    private val retryAttempts = AtomicInteger(0)

    suspend fun getViewing(channelId: String?, contentId: String): F1TvViewing = withContext(Dispatchers.IO) {
        Log.d(TAG, "getViewing $channelId - $contentId")
        retryAttempts.set(0)

        while (retryAttempts.getAndIncrement() < MAX_RETRIES) {
            try {
                return@withContext getViewingOrThrow(channelId, contentId)
            } catch (e: Exception) {
                Log.d(TAG, "Unable to fetch viewing", e)
                delay(RETRY_DELAY_IN_MILLS)
            }
        }

        throw MaxRetryAttemptsReachedException()
    }

    private suspend fun getViewingOrThrow(channelId: String?, contentId: String): F1TvViewing {
        val token = tokenService.loadAndGetF1Token()
        return f1Tv.getViewing(channelId, contentId, token.value)
    }

    class MaxRetryAttemptsReachedException: IllegalStateException("Max retries of $MAX_RETRIES reached, unable to fetch viewing")
}
