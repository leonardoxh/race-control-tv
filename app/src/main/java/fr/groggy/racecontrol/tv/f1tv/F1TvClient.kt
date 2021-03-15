package fr.groggy.racecontrol.tv.f1tv

import android.net.Uri
import android.util.Log
import com.auth0.android.jwt.JWT
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.f1.F1Token
import fr.groggy.racecontrol.tv.utils.http.execute
import fr.groggy.racecontrol.tv.utils.http.parseJsonBody
import fr.groggy.racecontrol.tv.utils.http.toJsonRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.Year
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
        private const val ROOT_URL = "https://f1tv.formula1.com/2.0/R/ENG/BIG_SCREEN_HLS"

        private const val GROUP_ID = 14 //TODO this might need to be migrated to the correct ONE
        private const val LIST_SEASON = "/ALL/PAGE/SEARCH/VOD/F1_TV_Pro_Monthly/$GROUP_ID?filter_objectSubtype=Meeting&filter_season=%s&filter_fetchAll=Y&filter_orderByFom=Y"
        private const val LIST_SESSIONS = "/ALL/PAGE/SANDWICH/F1_TV_Pro_Monthly/$GROUP_ID?meetingId=%s&title=weekend-sessions"
    }

    private val authenticateRequestJsonAdapter = moshi.adapter(F1TvAuthenticateRequest::class.java)
    private val authenticateResponseJsonAdapter =
        moshi.adapter(F1TvAuthenticateResponse::class.java)
    private val seasonResponseJsonAdapter = moshi.adapter(F1TvSeasonResponse::class.java)
    private val sessionResponseJsonAdapter = moshi.adapter(F1TvSessionResponse::class.java)
    private val imageResponseJsonAdapter = moshi.adapter(F1TvImageResponse::class.java)
    private val channelResponseJsonAdapter = moshi.adapter(F1TvChannelResponse::class.java)
    private val driverResponseJsonAdapter = moshi.adapter(F1TvDriverResponse::class.java)
    private val viewingRequestJsonAdapter = moshi.adapter(F1TvViewingRequest::class.java)
    private val viewingResponseJsonAdapter = moshi.adapter(F1TvViewingResponse::class.java)

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

    suspend fun getSeason(archive: Archive): F1TvSeason {
        val response = get(LIST_SEASON.format(archive.year), seasonResponseJsonAdapter)
        Log.d(TAG, "Fetched season $archive")
        return F1TvSeason(
            year = Year.of(archive.year),
            title = archive.year.toString(), //TODO - Use a proper name somehow
            events = response.resultObj.containers.map {
                F1TvSeasonEvent(
                    id = it.id,
                    meetingKey = it.metadata.emfAttributes.meetingKey,
                    title = it.metadata.emfAttributes.title
                )
            }
        )
    }

    suspend fun getSessions(event: F1TvSeasonEvent): List<F1TvSession> {
        try {
            val response = get(LIST_SESSIONS.format(event.meetingKey), sessionResponseJsonAdapter)
            Log.d(TAG, "Fetched session ${event.id}")

            return response.resultObj.containers.map {
                F1TvSession(
                    id = F1TvSessionId(it.id),
                    eventId = event.id,
                    name = it.metadata.title,
                    status = F1TvSessionStatus.from(it.metadata.contentSubtype),
                    period = InstantPeriod( //TODO derive this?
                        start = OffsetDateTime.now().toInstant(),
                        end = OffsetDateTime.now().toInstant()
                    ),
                    available = true,
                    images = listOf(),
                    channels = listOf() //TODO get of channels url
                )
            }
        } catch (e: Exception) {
            /* The pre seasons for example are not available to query */
            //TODO what do we do here?
            return listOf()
        }
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

    private suspend fun <T> get(apiUrl: String, jsonAdapter: JsonAdapter<T>): T {
        val request = Request.Builder()
            .url("$ROOT_URL$apiUrl")
            .get()
            .build()
        return request.execute(httpClient).parseJsonBody(jsonAdapter)
    }

}
