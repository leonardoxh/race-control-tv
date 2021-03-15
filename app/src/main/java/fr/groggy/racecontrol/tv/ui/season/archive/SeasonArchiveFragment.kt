package fr.groggy.racecontrol.tv.ui.season.archive

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity

@Keep
@AndroidEntryPoint
class SeasonArchiveFragment: VerticalGridSupportFragment(), OnItemViewClickedListener {
    private val itemAdapter: ArrayObjectAdapter by lazy {
        ArrayObjectAdapter(ArchivePresenter())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getText(R.string.choose_a_season)
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = 5
        }
        adapter = itemAdapter
        onItemViewClickedListener = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SeasonArchiveViewModel by viewModels()
        val archives = viewModel.listArchive()
        itemAdapter.setItems(archives, null)
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
