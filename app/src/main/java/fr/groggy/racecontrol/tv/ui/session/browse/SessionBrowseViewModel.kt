package fr.groggy.racecontrol.tv.ui.session.browse

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import fr.groggy.racecontrol.tv.core.channel.ChannelRepository
import fr.groggy.racecontrol.tv.core.image.ImageRepository
import fr.groggy.racecontrol.tv.core.session.SessionRepository
import fr.groggy.racecontrol.tv.core.session.SessionService
import fr.groggy.racecontrol.tv.f1tv.*
import fr.groggy.racecontrol.tv.f1tv.F1TvImageType.Companion.Headshot
import fr.groggy.racecontrol.tv.ui.DataClassByIdDiffCallback
import fr.groggy.racecontrol.tv.ui.channel.BasicChannelCard
import fr.groggy.racecontrol.tv.ui.channel.OnboardChannelCard
import fr.groggy.racecontrol.tv.utils.coroutines.traverse
import kotlinx.coroutines.flow.*

class SessionBrowseViewModel @ViewModelInject constructor(
    private val channelRepository: ChannelRepository,
    private val imageRepository: ImageRepository,
    private val sessionRepository: SessionRepository,
    private val sessionService: SessionService
) : ViewModel() {

    companion object {
        private val TAG = SessionBrowseViewModel::class.simpleName
    }

    suspend fun sessionLoaded(sessionId: String, contentId: String): Session {
        sessionService.loadChannels(contentId)
        return session(sessionId, contentId).first()
    }

    fun session(sessionId: String, contentId: String): Flow<Session> {
        return sessionRepository.observeById(sessionId)
            .onEach { Log.d(TAG, "Session changed") }
            .flatMapLatest { session ->
                val channelList = channels(contentId).first()

                channelList.singleOrNull()
                    ?.let { channel -> flowOf(
                        SingleChannelSession(
                            contentId = session.contentId,
                            channel = channel.id
                        )
                    ) }
                    ?: flowOf(
                        MultiChannelsSession(
                            contentId = session.contentId,
                            name = session.name,
                            channels = channelList
                        )
                    )
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM session changed") }
    }

    private fun channels(contentId: String): Flow<List<Channel>> =
        channelRepository.observe(contentId)
            .onEach { Log.d(TAG, "Channels changed") }
            .flatMapLatest { channels -> channels
                //.sortedBy { channel -> ids.indexOfFirst { it == channel.id } }
                .traverse { channel -> when (channel) {
                    is F1TvBasicChannel -> flowOf(BasicChannel(
                        id = F1TvChannelId(channel.channelId),
                        contentId = channel.contentId,
                        type = channel.type
                    ))
                    is F1TvOnboardChannel -> driver(channel.driver)
                        .map { driver -> OnboardChannel(
                            id = F1TvChannelId(channel.channelId),
                            contentId = channel.contentId,
                            name = channel.name,
                            driver = driver
                        ) }
                } }
            }
            .distinctUntilChanged()
            .onEach { Log.d(TAG, "VM channels changed") }

    private fun driver(id: F1TvDriverId): Flow<Driver?> {
        return flowOf(null)
//        return driverRepository.observe(id)
//            .onEach { Log.d(TAG, "Driver changed") }
//            .filterNotNull()
//            .flatMapLatest { driver -> headshot(driver.images)
//                .map { headshot -> Driver(
//                    id = driver.id,
//                    racingNumber = driver.racingNumber,
//                    headshot = headshot
//                ) }
//            }
//            .distinctUntilChanged()
//            .onEach { Log.d(TAG, "VM driver changed") }
    }

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
    abstract val contentId: String
}

data class SingleChannelSession(
    override val contentId: String,
    val channel: F1TvChannelId
) : Session()

data class MultiChannelsSession(
    override val contentId: String,
    val name: String,
    val channels: List<Channel>
) : Session()

sealed class Channel {

    companion object {
        val diffCallback = DataClassByIdDiffCallback { channel: Channel -> channel.id }
    }

    abstract val id: F1TvChannelId
    abstract val contentId: String

}

data class BasicChannel(
    override val id: F1TvChannelId,
    override val contentId: String,
    override val type: F1TvBasicChannelType
) : Channel(), BasicChannelCard

data class OnboardChannel(
    override val id: F1TvChannelId,
    override val contentId: String,
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
