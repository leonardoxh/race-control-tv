package fr.groggy.racecontrol.tv.ui.home

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R

@AndroidEntryPoint
class HomeActivity : FragmentActivity(R.layout.activity_home) {
    companion object {
        fun intent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}