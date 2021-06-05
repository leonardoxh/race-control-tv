package fr.groggy.racecontrol.tv.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.common.CustomListRowPresenter
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import fr.groggy.racecontrol.tv.ui.season.browse.Season
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity
import fr.groggy.racecontrol.tv.ui.season.browse.Session
import fr.groggy.racecontrol.tv.ui.session.SessionCardPresenter
import fr.groggy.racecontrol.tv.ui.session.browse.SessionBrowseActivity
import org.threeten.bp.Year

@Keep
@AndroidEntryPoint
class HomeFragment : RowsSupportFragment(), OnItemViewClickedListener {

    private val homeEntriesAdapter = ArrayObjectAdapter(CustomListRowPresenter())
    private var imageView: ImageView? = null
    private val currentYear = Year.now().value
    private var archivesRow: ListRow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIElements()
        setupEventListeners()
        buildRowsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            val dimensionPixelSize =
                inflater.context.resources.getDimensionPixelSize(R.dimen.lb_browse_rows_fading_edge)
            val horizontalMargin = -dimensionPixelSize * 2 - 4

            leftMargin = horizontalMargin
            rightMargin = horizontalMargin
        }

        imageView = requireActivity().findViewById(R.id.teaserImage)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView?.requestFocus()
        imageView?.setOnClickListener {
            val activity = SeasonBrowseActivity.intent(requireContext(), Archive(currentYear))
            startActivity(activity)
        }

        val teaserImageText = requireActivity().findViewById<TextView>(R.id.teaserImageText)
        teaserImageText.text = resources.getString(R.string.teaser_image_text, currentYear)
        homeEntriesAdapter.clear()
    }

    private fun buildRowsAdapter() {
        val viewModel: HomeViewModel by viewModels()

        lifecycleScope.launchWhenStarted {
            viewModel.getCurrentSeason(Archive(currentYear)).asLiveData()
                .observe(viewLifecycleOwner, ::onUpdatedSeason)
        }

        archivesRow = getArchiveRow(viewModel)
    }

    private fun onUpdatedSeason(season: Season) {
        val events = season.events.filter { it.sessions.isNotEmpty() }
        if (events.isNotEmpty()) {
            val event = events.first()
            val existingListRows = homeEntriesAdapter.unmodifiableList<ListRow>()
            val headerName =
                getString(R.string.season_last_event, event.name, currentYear.toString())
            val existingListRow = existingListRows.find { it.headerItem.name == headerName }
            val sessionsListRow = getLastSessionsRow(event.sessions, headerName, existingListRow)

            if (existingListRow == null) {
                homeEntriesAdapter.add(sessionsListRow)
                homeEntriesAdapter.add(archivesRow)
            } else {
                homeEntriesAdapter.replace(0, sessionsListRow)
            }
        }
    }

    private fun getLastSessionsRow(
        sessions: List<Session>,
        headerName: String,
        existingListRow: ListRow?
    ): ListRow {
        val (listRow, listRowAdapter) = if (existingListRow == null) {
            val listRowAdapter = ArrayObjectAdapter(SessionCardPresenter())
            val listRow = ListRow(HeaderItem(headerName), listRowAdapter)
            listRow to listRowAdapter
        } else {
            val listRowAdapter = existingListRow.adapter as ArrayObjectAdapter
            existingListRow to listRowAdapter
        }
        listRowAdapter.setItems(sessions, Session.diffCallback)
        return listRow
    }

    private fun getArchiveRow(viewModel: HomeViewModel): ListRow {
        val archives = viewModel.listArchive().subList(1, 6)
            .map { archive -> HomeItem(HomeItemType.ARCHIVE, archive.year.toString()) }

        val listRowAdapter = ArrayObjectAdapter(HomeItemPresenter())
        listRowAdapter.setItems(archives, null)
        listRowAdapter.add(
            HomeItem(
                HomeItemType.ARCHIVE_ALL,
                resources.getString(R.string.home_all)
            )
        )

        return ListRow(HeaderItem(resources.getString(R.string.home_archive)), listRowAdapter)
    }

    private fun setupUIElements() {
        adapter = homeEntriesAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = this
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val activity = when (item) {
            is Session -> {
                SessionBrowseActivity.intent(requireActivity(), item.id.value, item.contentId)
            }
            is HomeItem -> when (item.type) {
                HomeItemType.ARCHIVE -> {
                    SeasonBrowseActivity.intent(requireContext(), Archive(item.text.toInt()))
                }
                HomeItemType.ARCHIVE_ALL -> {
                    SeasonArchiveActivity.intent(requireContext())
                }
            }
            else -> null
        }
        startActivity(activity)
    }
}
