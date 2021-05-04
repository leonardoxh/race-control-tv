package fr.groggy.racecontrol.tv.ui.season.archive

import android.os.Bundle
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity

@Keep
@AndroidEntryPoint
class SeasonArchiveFragment: BrowseSupportFragment(), OnItemViewClickedListener {

    private val archivesAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIElements()
        setupEventListeners()
        buildRowsAdapter()
    }

    private fun setupUIElements() {
        title = getText(R.string.choose_a_season)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        adapter = archivesAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = this
    }

    private fun buildRowsAdapter() {
        val archivesTitle = "%s - %s"
        val viewModel: SeasonArchiveViewModel by viewModels()
        val archives = viewModel.listArchive()
        val decades = archives.groupBy { archive: Archive -> (archive.year / 10) * 10 }
        decades.entries.forEach {decade ->
            run {
                val entries = decade.value
                val listRowAdapter = ArrayObjectAdapter(ArchivePresenter())
                listRowAdapter.setItems(entries, null)
                val title = archivesTitle.format(entries[entries.size - 1].year, entries[0].year)
                archivesAdapter.add(ListRow(HeaderItem(title), listRowAdapter))
            }
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val browseActivity = SeasonBrowseActivity
            .intent(requireContext(), item as Archive)
        startActivity(browseActivity)
    }
}
