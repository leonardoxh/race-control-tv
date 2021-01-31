package fr.groggy.racecontrol.tv.ui.season.archive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonId
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity

@AndroidEntryPoint
class SeasonArchiveFragment: VerticalGridSupportFragment(), OnItemViewClickedListener {
    private val itemAdapter: ArrayObjectAdapter by lazy {
        ArrayObjectAdapter(ArchivePresenter())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = 5
        }
        adapter = itemAdapter
        onItemViewClickedListener = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SeasonArchiveViewModel by viewModels()
        lifecycleScope.launchWhenStarted {
            val archives = viewModel.listArchive()
            bindUi(archives)
        }
    }

    private fun bindUi(archive: List<Archive>) {
        itemAdapter.setItems(archive, null)
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val archiveItem = item as Archive
        val browseActivity = SeasonBrowseActivity
            .intent(requireContext(), F1TvSeasonId.ofUid(archiveItem.uid))
        startActivity(browseActivity)
    }
}
