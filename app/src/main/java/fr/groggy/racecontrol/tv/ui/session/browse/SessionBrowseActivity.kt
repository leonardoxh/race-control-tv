package fr.groggy.racecontrol.tv.ui.session.browse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.session.SessionService
import fr.groggy.racecontrol.tv.f1tv.F1TvSessionId
import fr.groggy.racecontrol.tv.ui.channel.playback.ChannelPlaybackActivity
import javax.inject.Inject

@AndroidEntryPoint
class SessionBrowseActivity : FragmentActivity() {

    companion object {
        private val TAG = SessionBrowseActivity::class.simpleName

        fun intent(context: Context, sessionId: F1TvSessionId): Intent { //TODO?
            val intent = Intent(context, SessionBrowseActivity::class.java)
            //SessionGridFragment.putSession(intent, sessionId)
            return intent
        }
    }

    @Inject lateinit var sessionService: SessionService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_browse)

        val eventId = SessionGridFragment.findEventId(this@SessionBrowseActivity)
            ?: return finish()
        val viewModel: SessionBrowseViewModel by viewModels()
        lifecycleScope.launchWhenCreated {
            when (val session = viewModel.sessionLoaded(eventId)) {
                is SingleChannelSession -> {
                    val intent = ChannelPlaybackActivity.intent(this@SessionBrowseActivity, session.channel)
                    startActivity(intent)
                    finish()
                }
                is MultiChannelsSession -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SessionGridFragment::class.java, null)
                        .commit()
                }
            }
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        //TODO whatft?
        //lifecycleScope.launchWhenStarted { sessionService.loadSessionWithImagesAndChannels(sessionId) }
    }

}
