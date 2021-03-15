package fr.groggy.racecontrol.tv.core.token

import android.util.Log
import com.auth0.android.jwt.JWT
import fr.groggy.racecontrol.tv.core.credentials.CredentialsService
import fr.groggy.racecontrol.tv.f1.F1Client
import fr.groggy.racecontrol.tv.f1.F1Token
import fr.groggy.racecontrol.tv.f1tv.F1TvClient
import fr.groggy.racecontrol.tv.f1tv.F1TvToken
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenService @Inject constructor(
    private val f1TokenRepository: F1TokenRepository,
    private val f1TvTokenRepository: F1TvTokenRepository,
    private val credentialsService: CredentialsService,
    private val f1: F1Client,
    private val f1Tv: F1TvClient
) {

    companion object {
        private val TAG = TokenService::class.simpleName
        private val JWT_LEEWAY = Duration.ofMinutes(1)
    }

    suspend fun loadAndGetF1Token(): F1Token {
        Log.d(TAG, "loadAndGetF1Token")
        return loadAndGetToken(f1TokenRepository, { it.value }) {
            val credentials = credentialsService.getF1Credentials()
            f1.authenticate(credentials)
        }
    }

    suspend fun loadAndGetF1TvToken(): F1TvToken {
        Log.d(TAG, "loadAndGetF1TvToken")
        return loadAndGetToken(f1TvTokenRepository, { it.value }) {
            val f1Token = loadAndGetF1Token()
            f1Tv.authenticate(f1Token)
        }
    }

    private suspend fun <T> loadAndGetToken(repository: TokenRepository<T>, jwt: (T) -> JWT, fetch: suspend () -> T): T {
        val existingToken = repository.find()
        return if (existingToken == null || jwt(existingToken).isExpired(JWT_LEEWAY.seconds)) {
            val token = fetch()
            repository.save(token)
            token
        } else {
            existingToken
        }
    }

}
