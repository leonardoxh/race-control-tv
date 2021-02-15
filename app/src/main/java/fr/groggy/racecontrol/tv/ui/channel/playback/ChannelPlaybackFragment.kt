package fr.groggy.racecontrol.tv.ui.channel.playback

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.EventLogger
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.core.ViewingService
import fr.groggy.racecontrol.tv.f1tv.F1TvChannelId
import fr.groggy.racecontrol.tv.f1tv.F1TvViewing
import fr.groggy.racecontrol.tv.ui.player.ExoPlayerPlaybackTransportControlGlue
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class ChannelPlaybackFragment : VideoSupportFragment() {

    companion object {
        private val TAG = ChannelPlaybackFragment::class.simpleName

        private val CHANNEL_ID = "${ChannelPlaybackFragment::class}.CHANNEL_ID"

        fun putChannelId(intent: Intent, channelId: F1TvChannelId) {
            intent.putExtra(CHANNEL_ID, channelId.value)
        }

        fun findChannelId(activity: Activity): F1TvChannelId? =
            activity.intent.getStringExtra(CHANNEL_ID)?.let { F1TvChannelId(it) }
    }

    @Inject lateinit var viewingService: ViewingService
    @Inject lateinit var httpDataSourceFactory: HttpDataSource.Factory

    private val trackSelector: DefaultTrackSelector by lazy {
        DefaultTrackSelector(requireContext())
    }

    private val player: SimpleExoPlayer by lazy {
        val player = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        player.playWhenReady = true
        player.addAnalyticsListener(EventLogger(trackSelector))
        player
    }

    private val mediaSourceFactory: MediaSourceFactory by lazy {
        HlsMediaSource.Factory(httpDataSourceFactory)
            .setAllowChunklessPreparation(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        startPlayer()
    }

    private fun startPlayer() {
        val glue = ExoPlayerPlaybackTransportControlGlue(requireActivity(), player, trackSelector)
        glue.host = VideoSupportFragmentGlueHost(this)

        val channelId = findChannelId(requireActivity())!!
        lifecycleScope.launchWhenStarted {
            val viewing = viewingService.getViewing(channelId)
            onViewingCreated(viewing)
        }
    }

    private fun onViewingCreated(viewing: F1TvViewing) {
        if (!player.isPlaying) { //IF is playing already just ignore these calls
            val mediaSource = mediaSourceFactory.createMediaSource(viewing.url)
            player.prepare(mediaSource)
        }
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        player.release()
        super.onDestroy()
    }
}
