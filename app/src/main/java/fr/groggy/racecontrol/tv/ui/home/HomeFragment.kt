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
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import fr.groggy.racecontrol.tv.ui.season.browse.SeasonBrowseActivity
import org.threeten.bp.Year


@Keep
@AndroidEntryPoint
class HomeFragment : RowsSupportFragment(), OnItemViewClickedListener {

    private val listRowPresenter = ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE).apply {
        shadowEnabled = false
        selectEffectEnabled = false
    }
    private val archivesAdapter = ArrayObjectAdapter(listRowPresenter)

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
            val dimensionPixelSize = inflater.context.resources.getDimensionPixelSize(R.dimen.lb_browse_rows_fading_edge)
            val horizontalMargin = -dimensionPixelSize * 2 - 4

            leftMargin = horizontalMargin
            rightMargin = horizontalMargin
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentYear = Year.now().value

        val imageView = requireActivity().findViewById<ImageView>(R.id.teaserImage)
        imageView.setOnClickListener {
            val activity = SeasonBrowseActivity.intent(requireContext(), Archive(currentYear))
            startActivity(activity)
        }

        val teaserImageText = requireActivity().findViewById<TextView>(R.id.teaserImageText)
        teaserImageText.text = resources.getString(R.string.teaser_image_text, currentYear)
    }

    private fun buildRowsAdapter() {
        val viewModel: HomeViewModel by viewModels()

        archivesAdapter.add(getArchiveRow(viewModel))
//        archivesAdapter.add(ListRow(HeaderItem("Documentations"), listRowAdapter))
    }

    private fun getArchiveRow(viewModel: HomeViewModel): ListRow {
        val archives = viewModel.listArchive().subList(1, 6)
            .map { archive -> HomeItem(HomeItemType.ARCHIVE, archive.year.toString()) }

        val listRowAdapter = ArrayObjectAdapter(HomeItemPresenter())
        listRowAdapter.setItems(archives, null)
        listRowAdapter.add(HomeItem(HomeItemType.ARCHIVE_ALL, resources.getString(R.string.home_all)))

        return ListRow(HeaderItem(resources.getString(R.string.home_archive)), listRowAdapter)
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
        val activity = when ((item as HomeItem).type) {
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