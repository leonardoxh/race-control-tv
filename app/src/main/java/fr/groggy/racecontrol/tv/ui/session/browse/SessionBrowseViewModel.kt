package fr.groggy.racecontrol.tv.ui.session.browse

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.channel.ChannelRepository
import fr.groggy.racecontrol.tv.core.driver.DriverRepository
import fr.groggy.racecontrol.tv.core.image.ImageRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.f1tv.*
import fr.groggy.racecontrol.tv.f1tv.F1TvImageType.Companion.Headshot
import fr.groggy.racecontrol.tv.ui.DataClassByIdDiffCallback
import fr.groggy.racecontrol.tv.ui.channel.BasicChannelCard
import fr.groggy.racecontrol.tv.ui.channel.OnboardChannelCard
import fr.groggy.racecontrol.tv.utils.coroutines.traverse
import kotlinx.coroutines.flow.*

class SessionBrowseViewModel @ViewModelInject constructor(
    private val channelRepository: ChannelRepository,
    private val driverRepository: DriverRepository,
    private val imageRepository: ImageRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        private val TAG = SessionBrowseViewModel::class.simpleName
    }

    suspend fun sessionLoaded(id: F1TvEventId): Session =
        session(id)
            .filter { session -> when(session) {
                is SingleChannelSession -> true
                is MultiChannelsSession -> session.channels.isNotEmpty()
            } }
            .first()

    fun session(id: F1TvEventId): Flow<Session> =
        sessionRepository.observe(id)
            .onEach { Log.d(TAG, "Session changed") }
            .flatMapLatest { session ->
                session.channels.singleOrNull()
                    ?.let { channel -> flowOf(SingleChannelSession(
                        id = session.id,
                        channel = channel
                    )) }
                    ?: channels(session.channels)
                        .map { channels -> MultiChannelsSession(
                            id = session.id,
                            name = session.name,
                            channels = channels
                        ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM session changed") }

    private fun channels(ids: List<F1TvChannelId>): Flow<List<Channel>> =
        channelRepository.observe(ids)
            .onEach { Log.d(TAG, "Channels changed") }
            .flatMapLatest { channels -> channels
                .sortedBy { channel -> ids.indexOfFirst { it == channel.id } }
                .traverse { channel -> when(channel) {
                    is F1TvBasicChannel -> flowOf(BasicChannel(
                        id = channel.id,
                        type = channel.type
                    ))
                    is F1TvOnboardChannel -> driver(channel.driver)
                        .map { driver -> OnboardChannel(
                            id = channel.id,
                            name = channel.name,
                            driver = driver
                        ) }
                } }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM channels changed") }

    private fun driver(id: F1TvDriverId): Flow<Driver?> =
        driverRepository.observe(id)
            .onEach { Log.d(TAG, "Driver changed") }
            .filterNotNull()
            .flatMapLatest { driver -> headshot(driver.images)
                .map { headshot -> Driver(
                    id = driver.id,
                    racingNumber = driver.racingNumber,
                    headshot = headshot
                ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM driver changed") }

    private fun headshot(ids: List<F1TvImageId>): Flow<Image?> =
        imageRepository.observe(ids)
            .onEach { Log.d(TAG, "Images changed") }
            .map { images -> images
                .find { it.type == Headshot }
                ?.let { Image(
                    id = it.id,
                    url = it.url
                ) }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM headshot changed") }

}

sealed class Session {
    abstract val id: F1TvSessionId
}

data class SingleChannelSession(
    override val id: F1TvSessionId,
    val channel: F1TvChannelId
) : Session()

data class MultiChannelsSession(
    override val id: F1TvSessionId,
    val name: String,
    val channels: List<Channel>
) : Session()

sealed class Channel {

    companion object {
        val diffCallback = DataClassByIdDiffCallback { channel: Channel -> channel.id }
    }

    abstract val id: F1TvChannelId

}

data class BasicChannel(
    override val id: F1TvChannelId,
    override val type: F1TvBasicChannelType
) : Channel(), BasicChannelCard

data class OnboardChannel(
    override val id: F1TvChannelId,
    override val name: String,
    override val driver: Driver?
) : Channel(), OnboardChannelCard

data class Driver(
    val id: F1TvDriverId,
    override val racingNumber: Int,
    override val headshot: Image?
) : OnboardChannelCard.Driver

data class Image(
    val id: F1TvImageId,
    override val url: Uri
) : OnboardChannelCard.Image
