package fr.groggy.racecontrol.tv.ui.channel.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R

@AndroidEntryPoint
class ChannelPlaybackActivity : FragmentActivity() {

    companion object {
        private val TAG = ChannelPlaybackActivity::class.simpleName

        fun intent(context: Context, channelId: String?, contentId: String): Intent {
            val intent = Intent(context, ChannelPlaybackActivity::class.java)
            ChannelPlaybackFragment.putChannelId(
                intent,
                channelId
            )
            ChannelPlaybackFragment.putContentId(
                intent,
                contentId
            )
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_playback)
    }

}
