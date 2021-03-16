package fr.groggy.racecontrol.tv.f1tv

import android.net.Uri
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.utils.http.execute
import fr.groggy.racecontrol.tv.utils.http.parseJsonBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Instant
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
        private const val ROOT_URL = "https://f1tv.formula1.com"

        private const val GROUP_ID = 14 //TODO this might need to be migrated to the correct ONE
        private const val LIST_SEASON = "/2.0/R/ENG/BIG_SCREEN_HLS/ALL/PAGE/SEARCH/VOD/F1_TV_Pro_Monthly/$GROUP_ID?filter_objectSubtype=Meeting&filter_season=%s&filter_fetchAll=Y&filter_orderByFom=Y"
        private const val LIST_SESSIONS = "/2.0/R/ENG/BIG_SCREEN_HLS/ALL/PAGE/SANDWICH/F1_TV_Pro_Monthly/$GROUP_ID?meetingId=%s&title=weekend-sessions"
        private const val LIST_CHANNELS = "/2.0/R/ENG/BIG_SCREEN_HLS/ALL/CONTENT/VIDEO/%s/F1_TV_Pro_Monthly/${GROUP_ID}"
        private const val PICTURE_URL = "https://ott.formula1.com/image-resizer/image/%s?w=384&h=384&o=L"
    }

    private val seasonResponseJsonAdapter = moshi.adapter(F1TvSeasonResponse::class.java)
    private val sessionResponseJsonAdapter = moshi.adapter(F1TvSessionResponse::class.java)
    private val imageResponseJsonAdapter = moshi.adapter(F1TvImageResponse::class.java)
    private val channelResponseJsonAdapter = moshi.adapter(F1TvChannelResponse::class.java)
    private val driverResponseJsonAdapter = moshi.adapter(F1TvDriverResponse::class.java)
    private val sessionArchiveJsonAdapter = moshi.adapter(SessionArchive::class.java)
    private val archiveSortInstant = Instant.now()

    suspend fun getSeason(archive: Archive): F1TvSeason {
        val response = get(LIST_SEASON.format(archive.year), seasonResponseJsonAdapter)
        Log.d(TAG, "Fetched season $archive")
        return F1TvSeason(
            year = Year.of(archive.year),
            title = archive.year.toString(),
            events = response.resultObj.containers.map {
                F1TvSeasonEvent(
                    id = it.id,
                    meetingKey = it.metadata.emfAttributes.meetingKey,
                    title = it.metadata.emfAttributes.title
                )
            },
            detailAction = response.resultObj.containers.firstOrNull()?.actions?.firstOrNull { it.targetType == "DETAILS_PAGE" }?.uri
        )
    }

    suspend fun getSessions(event: F1TvSeasonEvent, season: F1TvSeason): List<F1TvSession> {
        return if (season.year.value < 2018) {
            getSessionArchive(event, season)
        } else {
            getF1TvSessions(event)
        }
    }

    private suspend fun getSessionArchive(event: F1TvSeasonEvent, season: F1TvSeason): List<F1TvSession> {
        try {
            val result = get(season.detailAction!!, sessionArchiveJsonAdapter)
            return result.resultObj.containers.mapNotNull { sessionArchiveContainer ->
                sessionArchiveContainer.retrieveItems.resultObj.containers
            }.flatten().map {
                F1TvSession(
                    id = F1TvSessionId(it.id),
                    eventId = event.id,
                    pictureUrl = PICTURE_URL.format(it.metadata.pictureUrl),
                    contentId = it.metadata.contentId,
                    name = it.metadata.title,
                    status = F1TvSessionStatus.from(it.metadata.contentSubtype),
                    period = InstantPeriod(
                        start = archiveSortInstant,
                        end = archiveSortInstant
                    ),
                    available = true,
                    images = listOf(),
                    channels = listOf()
                )
            }
        } catch (_: Exception) {
            return listOf()
        }
    }

    private suspend fun getF1TvSessions(event: F1TvSeasonEvent): List<F1TvSession> {
        try {
            val response = get(LIST_SESSIONS.format(event.meetingKey), sessionResponseJsonAdapter)
            Log.d(TAG, "Fetched session ${event.id}")

            return response.resultObj.containers.map {
                F1TvSession(
                    id = F1TvSessionId(it.id),
                    eventId = event.id,
                    pictureUrl = PICTURE_URL.format(it.metadata.pictureUrl),
                    contentId = it.metadata.contentId,
                    name = it.metadata.title,
                    status = F1TvSessionStatus.from(it.metadata.contentSubtype),
                    period = InstantPeriod(
                        start = parseOffsetDateSafely(it.metadata.emfAttributes.startDate),
                        end = parseOffsetDateSafely(it.metadata.emfAttributes.endDate)
                    ),
                    available = true,
                    images = listOf(),
                    channels = listOf()
                )
            }
        } catch (_: Exception) {
            /* The pre seasons for example are not available to query */
            return listOf()
        }
    }

    /*
     * Addresses issues with F1 dates
     * sometimes the start date is missing
     */
    private fun parseOffsetDateSafely(date: String): Instant {
        return try {
            OffsetDateTime.parse(date).toInstant()
        } catch (_: Exception) {
            archiveSortInstant //Less than ideal but at least we can see something
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

    suspend fun getChannels(contentId: String): List<F1TvChannel> {
        try {
            val response = get(LIST_CHANNELS.format(contentId), channelResponseJsonAdapter)
            return response.resultObj.containers.firstOrNull()?.metadata?.additionalStreams?.map {
                val channelIdAndContentId = it.playbackUrl.split("CONTENT/PLAY?")
                    .last()
                    .split('&')

                if (it.type == "obc") {
                    F1TvOnboardChannel(
                        channelId = channelIdAndContentId.first().split("=").last(),
                        contentId = channelIdAndContentId.last().split("=").last(),
                        name = it.title,
                        driver = F1TvDriverId("") //TODO - do we have to load the driver ?
                    )
                } else {
                    F1TvBasicChannel(
                        channelId = channelIdAndContentId.first().split("=").last(),
                        contentId = channelIdAndContentId.last().split("=").last(),
                        type = F1TvBasicChannelType.from(it.type, it.title)
                    )
                }
            } ?: listOf()
        } catch (e: Exception) {
            return listOf()
        }
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

    private suspend fun <T> get(apiUrl: String, jsonAdapter: JsonAdapter<T>): T {
        val request = Request.Builder()
            .url("$ROOT_URL$apiUrl")
            .get()
            .build()
        return request.execute(httpClient).parseJsonBody(jsonAdapter)
    }

}
