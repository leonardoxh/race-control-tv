package fr.groggy.racecontrol.tv.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveViewModel


@Keep
@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment(), OnItemViewClickedListener {

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
        val containerDock = view!!.findViewById<View>(R.id.browse_container_dock) as FrameLayout
        val params = containerDock.layoutParams as MarginLayoutParams
        val resources: Resources = inflater.context.resources
        val newMarginTop =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
                .toInt()

        val offsetTopToZero: Int =
            -resources.getDimensionPixelSize(R.dimen.lb_browse_rows_margin_top)
        val dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.lb_browse_rows_fading_edge)
        val horizontalMargin = -dimensionPixelSize * 2 - 4

        params.topMargin = offsetTopToZero + newMarginTop
        params.leftMargin = horizontalMargin
        params.rightMargin = horizontalMargin
        containerDock.layoutParams = params
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = requireActivity().findViewById<ImageView>(R.id.imageView)
        Log.d("HOME FRAGMENT", imageView.toString())
        imageView?.setOnClickListener {
            Log.d("HOME FRAGMENT", "IMAGE")
        }
    }

    private fun buildRowsAdapter() {
        val viewModel: SeasonArchiveViewModel by viewModels()
        val archives = viewModel.listArchive().subList(1, 6)
            .map { archive -> HomeItem(HomeItemType.ITEM, archive.year.toString()) }

        val listRowAdapter = ArrayObjectAdapter(HomeItemPresenter())
        listRowAdapter.setItems(archives, null)
        listRowAdapter.add(HomeItem(HomeItemType.ALL, "Alle anzeigen"))
        archivesAdapter.add(ListRow(HeaderItem("Archiv"), listRowAdapter))
    }

    private fun setupUIElements() {
        headersState = HEADERS_DISABLED
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
        Log.d("HOME FRAGMENT", (item as HomeItem).text)
        Log.d("HOME FRAGMENT", item.type.name)
//        val browseActivity = SeasonBrowseActivity
//            .intent(requireContext(), item as Archive)
//        startActivity(browseActivity)
    }
}