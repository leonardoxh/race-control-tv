package fr.groggy.racecontrol.tv.ui.season.browse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.channel.playback.ChannelPlaybackActivity
import fr.groggy.racecontrol.tv.ui.event.EventListRowDiffCallback
import fr.groggy.racecontrol.tv.ui.session.SessionCardPresenter
import fr.groggy.racecontrol.tv.ui.session.browse.SessionBrowseActivity
import org.threeten.bp.Year
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class SeasonBrowseFragment : BrowseSupportFragment(), OnItemViewClickedListener {

    companion object {
        private val TAG = SeasonBrowseFragment::class.simpleName

        private val YEAR = "${SeasonBrowseFragment::class}.YEAR"

        fun putArchive(intent: Intent, archive: Archive) {
            intent.putExtra(YEAR, archive.year)
        }

        fun findArchive(activity: Activity): Archive {
            val year = activity.intent.getIntExtra(
                YEAR, Year.now().value
            )
            return Archive(year)
        }
    }

    @Inject lateinit var eventListRowDiffCallback: EventListRowDiffCallback
    @Inject lateinit var sessionCardPresenter: SessionCardPresenter

    private lateinit var eventsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setupUIElements()
        setupEventListeners()

        val viewModel: SeasonBrowseViewModel by viewModels({ requireActivity() })
        val archive = findArchive(requireActivity())
        lifecycleScope.launchWhenStarted {
            viewModel.season(archive).asLiveData().observe(viewLifecycleOwner, ::onUpdatedSeason)
        }
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        eventsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = eventsAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = this
    }

    private fun onUpdatedSeason(season: Season) {
        title = season.name
        val existingListRows = eventsAdapter.unmodifiableList<ListRow>()
        val events = season.events
            .filter { it.sessions.isNotEmpty() }
            .map { toListRow(it, existingListRows) }
        eventsAdapter.setItems(events, eventListRowDiffCallback)
    }

    private fun toListRow(event: Event, existingListRows: List<ListRow>): ListRow {
        val existingListRow = existingListRows.find { it.headerItem.name == event.name }
        val (listRow, sessionsAdapter) = if (existingListRow == null) {
            val sessionsAdapter = ArrayObjectAdapter(sessionCardPresenter)
            val listRow = ListRow(HeaderItem(event.name), sessionsAdapter)
            listRow to sessionsAdapter
        } else {
            val sessionsAdapter = existingListRow.adapter as ArrayObjectAdapter
            existingListRow to sessionsAdapter
        }
        sessionsAdapter.setItems(event.sessions, Session.diffCallback)
        return listRow
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row) {
        val session = item as Session
        val intent = session.channels.singleOrNull()
            ?.run { ChannelPlaybackActivity.intent(requireActivity(), value, session.contentId) }
            //TODO MULTIPLE CHANNELS
            ?: SessionBrowseActivity.intent(requireActivity(), session.id)
        startActivity(intent)
    }

}
