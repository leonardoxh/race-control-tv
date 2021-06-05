package fr.groggy.racecontrol.tv.f1tv

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fr.groggy.racecontrol.tv.core.InstantPeriod
import fr.groggy.racecontrol.tv.utils.http.execute
import fr.groggy.racecontrol.tv.utils.http.parseJsonBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.toImmutableList
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.Year
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class F1TvClient @Inject constructor(
    private val httpClient: OkHttpClient,
    moshi: Moshi
) {

    companion object {
        private val TAG = F1TvClient::class.simpleName
        private const val ROOT_URL = "https://f1tv.formula1.com"

        private const val GROUP_ID = 2 //TODO this might need to be migrated to the correct ONE
        private const val LIST_SEASON = "/2.0/R/%s/BIG_SCREEN_HLS/ALL/PAGE/SEARCH/VOD/F1_TV_Pro_Monthly/$GROUP_ID?filter_objectSubtype=Meeting&filter_season=%s&filter_orderByFom=Y&maxResults=100"
        private const val LIST_SESSIONS = "/2.0/R/%s/BIG_SCREEN_HLS/ALL/PAGE/SANDWICH/F1_TV_Pro_Monthly/$GROUP_ID?meetingId=%s&title=weekend-sessions"
        private const val LIST_FUTURE_SESSIONS = "/2.0/R/%s/BIG_SCREEN_HLS/ALL/PAGE/1350/F1_TV_Pro_Monthly/$GROUP_ID"
        private const val LIST_CHANNELS = "/2.0/R/%s/BIG_SCREEN_HLS/ALL/CONTENT/VIDEO/%s/F1_TV_Pro_Monthly/$GROUP_ID"
        private const val PICTURE_URL = "https://ott.formula1.com/image-resizer/image/%s?w=384&h=384&o=L&q=HI"
    }

    private val seasonResponseJsonAdapter = moshi.adapter(F1TvSeasonResponse::class.java)
    private val sessionResponseJsonAdapter = moshi.adapter(F1TvSessionResponse::class.java)
    private val futureSessionResponseJsonAdapter = moshi.adapter(F1TvFutureSessionResponse::class.java)
    private val channelResponseJsonAdapter = moshi.adapter(F1TvChannelResponse::class.java)
    private val sessionArchiveJsonAdapter = moshi.adapter(SessionArchive::class.java)
    private val archiveSortInstant = Instant.now()

    suspend fun getSeason(archive: Archive): F1TvSeason {
        val response = get(LIST_SEASON.format(getCurrentLocale(), archive.year), seasonResponseJsonAdapter)
        Log.d(TAG, "Fetched season $archive")
        return F1TvSeason(
            year = Year.of(archive.year),
            title = archive.year.toString(),
            events = response.resultObj.containers.map {
                F1TvSeasonEvent(
                    id = it.id,
                    meetingKey = it.metadata.emfAttributes.meetingKey,
                    title = it.metadata.emfAttributes.title,
                    period = InstantPeriod(
                        start = parseOffsetDateSafely(it.metadata.emfAttributes.startDate),
                        end = parseOffsetDateSafely(it.metadata.emfAttributes.endDate)
                    )
                )
            },
            detailAction = response.resultObj.containers.firstOrNull()?.actions?.firstOrNull { it.targetType == "DETAILS_PAGE" }?.uri
        )
    }

    suspend fun getSessions(event: F1TvSeasonEvent, season: F1TvSeason): List<F1TvSession> {
        return when {
            season.year.value < 2018 -> {
                getSessionArchive(event, season)
            }
            event.period.start < Instant.now() -> {
                getF1TvSessions(event)
            }
            else -> {
                return listOf()
            }
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
                    contentSubtype = it.metadata.contentSubtype,
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
        val list = mutableListOf<F1TvSession>()
        if (event.period.start < Instant.now() && event.period.end > Instant.now()) {
            list.addAll(getFutureF1TvSessions(event))
        }
        list.addAll(getBroadcastedF1TvSessions(event))
        list.forEach { it -> Log.d(TAG, it.toString()) }
        return list
    }

    private suspend fun getBroadcastedF1TvSessions(event: F1TvSeasonEvent): List<F1TvSession> {
        try {
            val response = get(
                LIST_SESSIONS.format(getCurrentLocale(), event.meetingKey),
                sessionResponseJsonAdapter
            )
            Log.d(TAG, "Fetched broadcasted sessions for event ${event.id}")
            return response.resultObj.containers.map {
                F1TvSession(
                    id = F1TvSessionId(it.id),
                    eventId = event.id,
                    pictureUrl = PICTURE_URL.format(it.metadata.pictureUrl),
                    contentId = it.metadata.contentId,
                    name = it.metadata.title,
                    contentSubtype = it.metadata.contentSubtype,
                    period = InstantPeriod(
                        start = parseOffsetDateSafely(it.metadata.emfAttributes.startDate),
                        end = parseOffsetDateSafely(it.metadata.emfAttributes.endDate)
                    ),
                    available = true,
                    images = listOf(),
                    channels = listOf()
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "getF1TvSessions failed with ${e.message}")
            return listOf()
        }
    }

    private suspend fun getFutureF1TvSessions(event: F1TvSeasonEvent): List<F1TvSession> {
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
        try {
            val response = get(
                LIST_FUTURE_SESSIONS.format(getCurrentLocale(), event.meetingKey),
                futureSessionResponseJsonAdapter
            )
            Log.d(TAG, "Fetched future sessions for event ${event.id}")
            val schedules = mutableListOf<F1TvFutureSessionEvent>()
            response.resultObj.containers
                .filter { it.layout == "schedule" }
                .forEach { it ->
                    it.retrieveItems.resultObj.containers
                        .filter { it.eventName.equals("ALL") }
                        .forEach { ev ->
                            ev.events!!
                                .filter {
                                    it.metadata.emfAttributes.sessionStartDate > Instant.now()
                                        .toEpochMilli()
                                }
                                .forEach { fev ->
                                    schedules.add(fev)
                                    Log.d(TAG, fev.toString())
                                }
                        }
                }
            return schedules.map {
                F1TvSession(
                    id = F1TvSessionId(it.id),
                    eventId = event.id,
                    pictureUrl = PICTURE_URL.format(it.metadata.pictureUrl),
                    contentId = it.metadata.contentId,
                    name = it.metadata.title,
                    contentSubtype = dateTimeFormatter.format(Instant.ofEpochMilli(it.metadata.emfAttributes.sessionStartDate)),
                    period = InstantPeriod(
                        start = Instant.ofEpochMilli(it.metadata.emfAttributes.sessionStartDate),
                        end = Instant.ofEpochMilli(it.metadata.emfAttributes.sessionEndDate)
                    ),
                    available = true,
                    images = listOf(),
                    channels = listOf()
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "getFutureF1TvSessions failed with ${e.message}")
            return listOf()
        }
    }

    /*
     * Addresses issues with F1 dates
     * sometimes the start date is missing
     */
    private fun parseOffsetDateSafely(date: String?): Instant {
        return try {
            OffsetDateTime.parse(date).toInstant()
        } catch (e: Exception) {
            Log.d(TAG, "Unable to parse date ${e.message}")
            archiveSortInstant //Less than ideal but at least we can see something
        }
    }

    suspend fun getChannels(contentId: String): List<F1TvChannel> {
        try {
            val response = get(LIST_CHANNELS.format(getCurrentLocale(), contentId), channelResponseJsonAdapter)
            return response.resultObj.containers.firstOrNull()?.metadata?.additionalStreams
                ?.sortedBy { it.racingNumber }
                ?.map {
                val channelIdAndContentId = it.playbackUrl.split("CONTENT/PLAY?")
                    .last()
                    .split('&')

                if (it.type == "obc") {
                    F1TvOnboardChannel(
                        channelId = channelIdAndContentId.first().split("=").last(),
                        contentId = channelIdAndContentId.last().split("=").last(),
                        name = "${it.driverFirstName} ${it.driverLastName} ${it.racingNumber}",
                        background = it.hex,
                        subTitle = it.teamName,
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

    private suspend fun <T> get(apiUrl: String, jsonAdapter: JsonAdapter<T>): T {
        val request = Request.Builder()
            .url("$ROOT_URL$apiUrl")
            .get()
            .build()
        return request.execute(httpClient).parseJsonBody(jsonAdapter)
    }

    private fun getCurrentLocale(): String {
        return when (val isO3Language = Locale.getDefault().isO3Language) {
            "deu", "fra", "nld", "spa", "por" -> isO3Language.toUpperCase(Locale.ROOT)
            else -> "ENG"
        }
    }
}
