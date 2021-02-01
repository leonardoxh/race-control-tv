package fr.groggy.racecontrol.tv.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.credentials.CredentialsService
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import fr.groggy.racecontrol.tv.ui.signin.SignInActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    @Inject lateinit var credentialsService: CredentialsService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startHomeActivity()
    }

    private fun startHomeActivity() {
        lifecycleScope.launchWhenStarted {
            val intent = if (credentialsService.hasValidF1Credentials()) {
                SeasonArchiveActivity.intent(this@MainActivity)
            } else {
                SignInActivity.intent(this@MainActivity)
            }
            startActivity(intent)
            finish()
        }
    }
}
