package fr.groggy.racecontrol.tv.ui.session.browse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.ui.channel.ChannelCardPresenter
import fr.groggy.racecontrol.tv.ui.channel.playback.ChannelPlaybackActivity
import javax.inject.Inject


@Keep
@AndroidEntryPoint
class SessionGridFragment : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        private val TAG = SessionGridFragment::class.simpleName

        private const val COLUMNS = 5

        private val CONTENT_ID = "${SessionGridFragment::class}.CONTENT_ID"
        private val SESSION_ID = "${SessionGridFragment::class}.SESSION_ID"

        fun putContentId(intent: Intent, contentId: String) {
            intent.putExtra(CONTENT_ID, contentId)
        }

        fun putSessionId(intent: Intent, sessionId: String) {
            intent.putExtra(SESSION_ID, sessionId)
        }

        fun findSessionId(activity: Activity): String? {
            return activity.intent.getStringExtra(SESSION_ID)
        }

        fun findContentId(activity: Activity): String? {
            return activity.intent.getStringExtra(CONTENT_ID)
        }
    }

    @Inject lateinit var channelsCardPresenter: ChannelCardPresenter

    private lateinit var channelsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setupUIElements()
        setupEventListeners()

        val sessionId = findSessionId(requireActivity()) ?: return requireActivity().finish()
        val contentId = findContentId(requireActivity()) ?: return requireActivity().finish()
        val viewModel: SessionBrowseViewModel by viewModels({ requireActivity() })
        viewModel.session(sessionId, contentId).asLiveData().observe(this, this::onUpdatedSession)
    }

    private fun setupUIElements() {
        gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = COLUMNS
        channelsAdapter = ArrayObjectAdapter(channelsCardPresenter)
        adapter = channelsAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = this
    }

    private fun onUpdatedSession(session: Session) {
        when (session) {
            is SingleChannelSession -> {
                val intent = ChannelPlaybackActivity.intent(
                    requireActivity(),
                    session.channel.value,
                    session.contentId
                )
                startActivity(intent)
                requireActivity().finish()
            }
            is MultiChannelsSession -> {
                title = session.name
                channelsAdapter.setItems(session.channels, Channel.diffCallback)
            }
        }
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        val channel = item as Channel
        val intent = ChannelPlaybackActivity.intent(requireActivity(), channel.id.value, channel.contentId)
        startActivity(intent)
    }

}
