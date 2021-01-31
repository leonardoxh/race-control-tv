package fr.groggy.racecontrol.tv.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.core.credentials.CredentialsService
import fr.groggy.racecontrol.tv.f1.F1Credentials
import fr.groggy.racecontrol.tv.ui.season.archive.SeasonArchiveActivity
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {

    companion object {
        private val TAG = SignInActivity::class.simpleName

        fun intent(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }

    @Inject lateinit var credentialsService: CredentialsService

    private val login by lazy { findViewById<EditText>(R.id.login) }
    private val password by lazy { findViewById<EditText>(R.id.password) }
    private val signIn by lazy { findViewById<Button>(R.id.signin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        signIn.setOnClickListener { onSignIn() }
        window.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE or SOFT_INPUT_ADJUST_PAN)
    }

    private fun onSignIn() {
        Log.d(TAG, "onSignIn")
        val credentials = F1Credentials(
            login = login.text.toString(),
            password = password.text.toString()
        )
        lifecycleScope.launchWhenStarted {
            if (credentials.login.isEmpty() || credentials.password.isEmpty()) {
                Toast.makeText(applicationContext, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
            } else if (credentialsService.checkAndSave(credentials)) {
                val intent = SeasonArchiveActivity.intent(this@SignInActivity)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, R.string.rejected_credentials, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
