package fr.groggy.racecontrol.tv.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller.EXTRA_STATUS
import android.content.pm.PackageInstaller.STATUS_SUCCESS
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.credentials.CredentialsService
import fr.groggy.racecontrol.tv.core.release.ApkService
import fr.groggy.racecontrol.tv.core.release.Release
import fr.groggy.racecontrol.tv.core.release.ReleaseService
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import fr.groggy.racecontrol.tv.ui.signin.SignInActivity
import io.noties.markwon.Markwon
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName

        private const val ACTION_INSTALL_RESULT = "INSTALL_RESULT"

        fun installResultIntentFilter(): IntentFilter =
            IntentFilter(ACTION_INSTALL_RESULT)

        fun installResultBroadcastIntent(extras: Bundle?): Intent {
            val intent = Intent(ACTION_INSTALL_RESULT)
            extras?.let { intent.putExtras(it) }
            return intent
        }
    }

    @Inject lateinit var releaseService: ReleaseService
    @Inject lateinit var credentialsService: CredentialsService
    @Inject lateinit var markwon: Markwon
    @Inject lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showReleaseUpdateDialogOrStartHomeActivity()
    }

    private fun showReleaseUpdateDialogOrStartHomeActivity() {
        lifecycleScope.launchWhenCreated {
            val newRelease = releaseService.findNewRelease()
            if (newRelease != null) {
                showReleaseUpdateDialog(newRelease)
            } else {
                startHomeActivity()
            }
        }
    }

    private fun showReleaseUpdateDialog(release: Release) {
        val dialog = ReleaseUpdateDialogFragment(
            release,
            { update(release) },
            { skip(release) },
            { lifecycleScope.launchWhenStarted { startHomeActivity() } },
            markwon
        )
        supportFragmentManager.beginTransaction()
            .add(dialog, null)
            .commit()
    }

    private fun update(release: Release) {
        localBroadcastManager.registerReceiver(InstallResultBroadcastReceiver(), installResultIntentFilter())
        val intent = ApkService.installIntent(this, release.apk)
        startService(intent)
    }

    private fun skip(release: Release) {
        lifecycleScope.launchWhenStarted {
            releaseService.dismiss(release.version)
            startHomeActivity()
        }
    }

    private suspend fun startHomeActivity() {
        val intent = if (credentialsService.hasValidF1Credentials()) {
            SeasonArchiveActivity.intent(this)
        //SeasonBrowseActivity.intent(this)
        } else {
            SignInActivity.intent(this)
        }
        startActivity(intent)
        finish()
    }

    inner class InstallResultBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            localBroadcastManager.unregisterReceiver(this)
            when (intent.getIntExtra(EXTRA_STATUS, -999)) {
                STATUS_SUCCESS -> {
                    Toast.makeText(applicationContext, R.string.new_version_installed, LENGTH_SHORT).show()
                    lifecycleScope.launchWhenStarted { startHomeActivity() }
                }
                else -> {
                    Log.e(TAG, "Fail to install new version : ${intent.extras}")
                    Toast.makeText(applicationContext, R.string.new_version_download_install_failed, LENGTH_SHORT).show()
                    lifecycleScope.launchWhenStarted { startHomeActivity() }
                }
            }
        }
    }

}
