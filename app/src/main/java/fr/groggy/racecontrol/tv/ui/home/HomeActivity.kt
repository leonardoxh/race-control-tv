package fr.groggy.racecontrol.tv.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.season.SeasonService
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.utils.coroutines.schedule
import org.threeten.bp.Duration
import org.threeten.bp.Year
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : FragmentActivity(R.layout.activity_home) {
    companion object {
        private val TAG = HomeActivity::class.simpleName

        fun intent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    @Inject
    internal lateinit var seasonService: SeasonService

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        lifecycleScope.launchWhenStarted {
            schedule(Duration.ofMinutes(1)) {
                Log.d("Fetching new data", "Lifecycle state is ${lifecycle.currentState}")
                val currentYear = Year.now().value
                seasonService.loadSeason(Archive(currentYear))
            }
        }
    }
}