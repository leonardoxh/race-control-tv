package fr.groggy.racecontrol.tv.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity
import org.threeten.bp.Year


@Keep
@AndroidEntryPoint
class HomeFragment : RowsSupportFragment(), OnItemViewClickedListener {

    private val archivesAdapter = ArrayObjectAdapter(ListRowPresenter())

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
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val params = view!!.layoutParams as ViewGroup.MarginLayoutParams
        val resources: Resources = inflater.context.resources

        val dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.lb_browse_rows_fading_edge)
        val horizontalMargin = -dimensionPixelSize * 2 - 4

        params.leftMargin = horizontalMargin
        params.rightMargin = horizontalMargin
        view.layoutParams = params
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = requireActivity().findViewById<ImageView>(R.id.imageView)
        imageView?.setOnClickListener {
            val activity = SeasonBrowseActivity.intent(requireContext(), Archive(Year.now().value))
            startActivity(activity)
        }
    }

    private fun buildRowsAdapter() {
        val viewModel: HomeViewModel by viewModels()
        val archives = viewModel.listArchive().subList(1, 6)
            .map { archive -> HomeItem(HomeItemType.ARCHIVE, archive.year.toString()) }

        val listRowAdapter = ArrayObjectAdapter(HomeItemPresenter())
        listRowAdapter.setItems(archives, null)
        listRowAdapter.add(HomeItem(HomeItemType.ARCHIVE_ALL, "Alle anzeigen"))
        archivesAdapter.add(ListRow(HeaderItem("Archiv"), listRowAdapter))
        archivesAdapter.add(ListRow(HeaderItem("Dokumentationen"), listRowAdapter))
    }

    private fun setupUIElements() {
        adapter = archivesAdapter
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
        val activity = when((item as HomeItem).type) {
            HomeItemType.ARCHIVE -> {
                SeasonBrowseActivity.intent(requireContext(), Archive(item.text.toInt()))
            }
            HomeItemType.ARCHIVE_ALL -> {
                SeasonArchiveActivity.intent(requireContext())
            }
        }
        startActivity(activity)
    }
}