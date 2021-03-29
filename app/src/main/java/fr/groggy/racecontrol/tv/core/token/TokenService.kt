package fr.groggy.racecontrol.tv.core.token

import android.util.Log
import com.auth0.android.jwt.JWT
import fr.groggy.racecontrol.tv.core.credentials.CredentialsService
import fr.groggy.racecontrol.tv.f1.F1Client
import fr.groggy.racecontrol.tv.f1.F1Token
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenService @Inject constructor(
    private val f1TokenRepository: F1TokenRepository,
    private val credentialsService: CredentialsService,
    private val f1: F1Client
) {

    companion object {
        private val TAG = TokenService::class.simpleName
        /*
         * This is a bit ridiculous
         * but it is a safe fallback to renew the token,
         * however it is always renewed at the start of the app
         * so most likely will never be used
         */
        private val JWT_LEEWAY = Duration.ofMinutes(1)
    }

    suspend fun loadAndGetF1Token(): F1Token {
        Log.d(TAG, "loadAndGetF1Token")
        return loadAndGetToken(f1TokenRepository, { it.value }) {
            val credentials = credentialsService.getF1Credentials()
            f1.authenticate(credentials)
        }
    }

    private suspend fun <T> loadAndGetToken(repository: TokenRepository<T>, jwt: (T) -> JWT, fetch: suspend () -> T): T {
        val existingToken = repository.find()
        return if (existingToken == null || jwt(existingToken).isExpired(JWT_LEEWAY.seconds)) {
            Log.d(TAG, "Token is expired fetching a new one")
            val token = fetch()
            repository.save(token)
            token
        } else {
            existingToken
        }
    }
}
