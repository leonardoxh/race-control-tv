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
import fr.groggy.racecontrol.tv.f1tv.F1TvEventId
import fr.groggy.racecontrol.tv.f1tv.F1TvSession
import fr.groggy.racecontrol.tv.ui.channel.ChannelCardPresenter
import fr.groggy.racecontrol.tv.ui.channel.playback.ChannelPlaybackActivity
import javax.inject.Inject


@Keep
@AndroidEntryPoint
class SessionGridFragment : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        private val TAG = SessionGridFragment::class.simpleName

        private const val COLUMNS = 5

        private val EVENT_ID = "${SessionGridFragment::class}.EVENT_ID"

        fun putSession(intent: Intent, session: F1TvSession) {
            intent.putExtra(EVENT_ID, session.eventId)
        }

        fun findEventId(activity: Activity): F1TvEventId? =
            activity.intent.getStringExtra(EVENT_ID)?.let(::F1TvEventId)
    }

    @Inject lateinit var channelsCardPresenter: ChannelCardPresenter

    private lateinit var channelsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setupUIElements()
        setupEventListeners()

        val eventId = findEventId(requireActivity()) ?: return requireActivity().finish()
        val viewModel: SessionBrowseViewModel by viewModels({ requireActivity() })
        viewModel.session(eventId).asLiveData().observe(this, this::onUpdatedSession)
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
        when(session) {
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
//TODO - channels
//        val channel = item as Channel
//        val intent = ChannelPlaybackActivity.intent(requireActivity(), channel.id)
//        startActivity(intent)
    }

}
