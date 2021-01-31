package fr.groggy.racecontrol.tv.f1tv

import android.net.Uri
import android.util.Log
import com.auth0.android.jwt.JWT
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.core.LocalDatePeriod
import fr.groggy.racecontrol.tv.f1.F1Token
import fr.groggy.racecontrol.tv.utils.http.execute
import fr.groggy.racecontrol.tv.utils.http.parseJsonBody
import fr.groggy.racecontrol.tv.utils.http.toJsonRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class F1TvClient @Inject constructor(
    private val httpClient: OkHttpClient,
    moshi: Moshi
) {

    companion object {
        private val TAG = F1TvClient::class.simpleName
        private const val IDENTITY_PROVIDER_URL = "/api/identity-providers/iden_732298a17f9c458890a1877880d140f3/"
        private const val AUTHENTICATE_URL = "https://f1tv-api.formula1.com/agl/1.0/unk/en/all_devices/global/authenticate"
        private const val ROOT_URL = "https://f1tv.formula1.com"
    }

    private val authenticateRequestJsonAdapter = moshi.adapter(F1TvAuthenticateRequest::class.java)
    private val authenticateResponseJsonAdapter =
        moshi.adapter(F1TvAuthenticateResponse::class.java)
    private val seasonResponseJsonAdapter = moshi.adapter(F1TvSeasonResponse::class.java)
    private val eventResponseJsonAdapter = moshi.adapter(F1TvEventResponse::class.java)
    private val sessionResponseJsonAdapter = moshi.adapter(F1TvSessionResponse::class.java)
    private val imageResponseJsonAdapter = moshi.adapter(F1TvImageResponse::class.java)
    private val channelResponseJsonAdapter = moshi.adapter(F1TvChannelResponse::class.java)
    private val driverResponseJsonAdapter = moshi.adapter(F1TvDriverResponse::class.java)
    private val viewingRequestJsonAdapter = moshi.adapter(F1TvViewingRequest::class.java)
    private val viewingResponseJsonAdapter = moshi.adapter(F1TvViewingResponse::class.java)
    private val archiveResponseJsonAdapter = moshi.adapter(ArchiveResponse::class.java)

    suspend fun authenticate(f1Token: F1Token): F1TvToken {
        val body = F1TvAuthenticateRequest(
            identityProviderUrl = IDENTITY_PROVIDER_URL,
            accessToken = f1Token.value.toString()
        ).toJsonRequestBody(authenticateRequestJsonAdapter)
        val request = Request.Builder()
            .url(AUTHENTICATE_URL)
            .post(body)
            .build()
        val response = request.execute(httpClient).parseJsonBody(authenticateResponseJsonAdapter)
        Log.d(TAG, "Authenticated")
        return F1TvToken(JWT(response.token))
    }

    suspend fun getSeason(id: F1TvSeasonId): F1TvSeason {
        val response = get(id.value, seasonResponseJsonAdapter)
        Log.d(TAG, "Fetched season $id")
        return F1TvSeason(
            id = F1TvSeasonId(response.self),
            name = response.name,
            year = Year.of(response.year),
            events = response.eventOccurrenceUrls.map { F1TvEventId(it) }
        )
    }

    suspend fun getEvent(id: F1TvEventId): F1TvEvent {
        val response = get(id.value, eventResponseJsonAdapter)
        Log.d(TAG, "Fetched event $id")
        return F1TvEvent(
            id = F1TvEventId(response.self),
            name = response.name,
            period = LocalDatePeriod(
                start = LocalDate.parse(response.startDate),
                end = LocalDate.parse(response.endDate)
            ),
            sessions = response.sessionOcurrenceUrls.map { F1TvSessionId(it) }
        )
    }

    suspend fun getSession(id: F1TvSessionId): F1TvSession {
        val response = get(id.value, sessionResponseJsonAdapter)
        Log.d(TAG, "Fetched session $id")
        return F1TvSession(
            id = F1TvSessionId(response.self),
            name = response.name,
            status = F1TvSessionStatus.from(response.status),
            period = InstantPeriod(
                start = OffsetDateTime.parse(response.startTime).toInstant(),
                end = OffsetDateTime.parse(response.endTime).toInstant()
            ),
            available = response.availabilityDetails.isAvailable,
            images = response.imageUrls.map { F1TvImageId(it) },
            channels = response.channelUrls.map { F1TvChannelId(it) }
        )
    }

    suspend fun getImage(id: F1TvImageId): F1TvImage {
        val response = get(id.value, imageResponseJsonAdapter)
        Log.d(TAG, "Fetched image $id")
        return F1TvImage(
            id = F1TvImageId(response.self),
            url = Uri.parse(response.url),
            type = F1TvImageType.from(response.type)
        )
    }

    suspend fun getChannel(id: F1TvChannelId): F1TvChannel {
        val response = get(id.value, channelResponseJsonAdapter)
        Log.d(TAG, "Fetched channel $id")
        return if (response.channelType == "driver") F1TvOnboardChannel(
            id = F1TvChannelId(response.self),
            name = response.name,
            driver = F1TvDriverId(response.driverOccurrenceUrls.first())
        ) else F1TvBasicChannel(
            id = F1TvChannelId(response.self),
            type = F1TvBasicChannelType.from(response.channelType, response.name)
        )
    }

    suspend fun getDriver(id: F1TvDriverId): F1TvDriver {
        val response = get(id.value, driverResponseJsonAdapter)
        Log.d(TAG, "Fetched driver $id")
        return F1TvDriver(
            id = F1TvDriverId(response.self),
            name = response.name,
            shortName = response.driverTla,
            racingNumber = response.driverRacingNumber,
            images = response.imageUrls.map { F1TvImageId(it) }
        )
    }

    suspend fun getViewing(channelId: F1TvChannelId, token: F1TvToken): F1TvViewing {
        val body = F1TvViewingRequest(
            channelUrl = channelId.value
        ).toJsonRequestBody(viewingRequestJsonAdapter)
        val request = Request.Builder()
            .url("$ROOT_URL/api/viewings")
            .post(body)
            .header("Authorization", "JWT ${token.value}")
            .build()
        val response = request.execute(httpClient).parseJsonBody(viewingResponseJsonAdapter)
        Log.d(TAG, "Fetched viewing url for channel $channelId")
        return F1TvViewing(
            url = Uri.parse(response.tokenisedUrl)
        )
    }

    suspend fun listArchive(): List<Archive> {
        return get("/api/race-season", archiveResponseJsonAdapter).objects
    }

    private suspend fun <T> get(apiUrl: String, jsonAdapter: JsonAdapter<T>): T {
        val request = Request.Builder()
            .url("$ROOT_URL$apiUrl")
            .get()
            .build()
        return request.execute(httpClient).parseJsonBody(jsonAdapter)
    }

}
