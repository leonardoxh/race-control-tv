package fr.groggy.racecontrol.tv.f1

import com.auth0.android.jwt.JWT
import com.squareup.moshi.Moshi
import fr.groggy.racecontrol.tv.utils.http.execute
import fr.groggy.racecontrol.tv.utils.http.parseJsonBody
import fr.groggy.racecontrol.tv.utils.http.toJsonRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class F1Client @Inject constructor(
    private val httpClient: OkHttpClient,
    moshi: Moshi
) {

    companion object {
        private const val ROOT_URL = "https://api.formula1.com"
        private const val API_KEY = "fCUCjWrKPu9ylJwRAv8BpGLEgiAuThx7"
    }

    private val authenticateRequestJsonAdapter = moshi.adapter(F1AuthenticateRequest::class.java)
    private val authenticateResponseJsonAdapter = moshi.adapter(F1AuthenticateResponse::class.java)

    suspend fun authenticate(credentials: F1Credentials): F1Token {
        val body = F1AuthenticateRequest(
            login = credentials.login,
            password = credentials.password
        ).toJsonRequestBody(authenticateRequestJsonAdapter)
        val request = Request.Builder()
            .url("${ROOT_URL}/v2/account/subscriber/authenticate/by-password")
            .post(body)
            .header("apiKey", API_KEY)
            .header("User-Agent", "RaceControl f1viewer")
            .build()
        val response = request.execute(httpClient).parseJsonBody(authenticateResponseJsonAdapter)
        return F1Token(JWT(response.data.subscriptionToken))
    }

}
