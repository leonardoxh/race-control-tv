package fr.groggy.racecontrol.tv.ui.season.archive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeasonArchiveFragment: VerticalGridSupportFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: SeasonArchiveViewModel by viewModels()
        lifecycleScope.launchWhenStarted {
            viewModel.listArchive()
        }
    }
}
