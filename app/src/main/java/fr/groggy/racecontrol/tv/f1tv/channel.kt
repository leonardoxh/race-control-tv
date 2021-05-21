package fr.groggy.racecontrol.tv.f1tv

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class F1TvChannelResponse(
    val resultObj: F1TvChannelResultObject
)

@JsonClass(generateAdapter = true)
data class F1TvChannelResultObject(
    val containers: List<F1TvChannelContainer>
)

@JsonClass(generateAdapter = true)
data class F1TvChannelContainer(
    val metadata: F1TvChannelMetadata
)

@JsonClass(generateAdapter = true)
data class F1TvChannelMetadata(
    val additionalStreams: List<F1TvChannelAdditionalStream>?
)

@JsonClass(generateAdapter = true)
data class F1TvChannelAdditionalStream(
    val title: String,
    val driverFirstName: String?,
    val driverLastName: String?,
    val racingNumber: Int?,
    val driverImg: String?, //For some reason this is always empty
    val playbackUrl: String,
    val teamName: String?,
    val hex: String?,
    val type: String
)

class F1TvChannelId(val value: String)

sealed class F1TvBasicChannelType {
    companion object {
        object Wif : F1TvBasicChannelType()
        object PitLane : F1TvBasicChannelType()
        object Tracker : F1TvBasicChannelType()
        object Data : F1TvBasicChannelType()
        data class Unknown(val type: String, val name: String) : F1TvBasicChannelType()

        fun from(type: String, name: String): F1TvBasicChannelType =
            when(type) {
                "wif" -> Wif
                "additional" -> when(name.toLowerCase(Locale.ROOT)) {
                    "pit lane" -> PitLane
                    "tracker" -> Tracker
                    "data" -> Data
                    else -> Unknown(type, name)
                }
                else -> Unknown(type, name)
            }
    }
}

sealed class F1TvChannel {
    abstract val channelId: String?
    abstract val contentId: String
}

data class F1TvBasicChannel(
    override val channelId: String?,
    override val contentId: String,
    val type: F1TvBasicChannelType
) : F1TvChannel()

data class F1TvOnboardChannel(
    override val channelId: String,
    override val contentId: String,
    val name: String,
    val background: String?,
    val subTitle: String?,
    val driver: F1TvDriverId
) : F1TvChannel()
