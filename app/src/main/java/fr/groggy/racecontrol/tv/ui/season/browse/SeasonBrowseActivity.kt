package fr.groggy.racecontrol.tv.ui.season.browse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.F1TvSeasonId
import fr.groggy.racecontrol.tv.utils.coroutines.schedule
import javax.inject.Inject
import kotlin.time.minutes

@AndroidEntryPoint
class SeasonBrowseActivity : FragmentActivity() {

    companion object {
        private val TAG = SeasonBrowseActivity::class.simpleName

        fun intent(context: Context): Intent =
            Intent(context, SeasonBrowseActivity::class.java)

        fun intent(context: Context, seasonId: F1TvSeasonId): Intent {
            val intent = intent(context)
            SeasonBrowseFragment.putSeasonId(
                intent,
                seasonId
            )
            return intent
        }
    }

    @Inject lateinit var seasonService: SeasonService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_browse)
        val viewModel: SeasonBrowseViewModel by viewModels()
        lifecycleScope.launchWhenCreated {
            val sessionId = SeasonBrowseFragment.findSeasonId(this@SeasonBrowseActivity)
            viewModel.seasonLoaded(sessionId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SeasonBrowseFragment::class.java, null)
                .commit()
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        lifecycleScope.launchWhenStarted {
            schedule(1.minutes) {
                val seasonId = SeasonBrowseFragment.findSeasonId(this@SeasonBrowseActivity)
                seasonService.loadSeason(seasonId)
            }
        }
    }

}
