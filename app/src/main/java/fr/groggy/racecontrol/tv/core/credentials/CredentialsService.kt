package fr.groggy.racecontrol.tv.core.credentials

import android.util.Log
import fr.groggy.racecontrol.tv.core.token.F1TokenRepository
import fr.groggy.racecontrol.tv.f1.F1Client
import fr.groggy.racecontrol.tv.f1.F1Credentials
import fr.groggy.racecontrol.tv.utils.http.HttpException
import kotlinx.coroutines.delay
import java.lang.Exception
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialsService @Inject constructor(
    private val f1CredentialsRepository: F1CredentialsRepository,
    private val f1TokenRepository: F1TokenRepository,
    private val f1: F1Client
) {

    companion object {
        private val TAG = CredentialsService::class.simpleName
    }

    suspend fun hasValidF1Credentials(deleteOnHttp403: Boolean = false): Boolean {
        val credentials = f1CredentialsRepository.find() ?: return false
        return try {
            val token = f1.authenticate(credentials)
            f1TokenRepository.save(token)
            true
        } catch (e: HttpException) {
            if (e.code == HttpURLConnection.HTTP_FORBIDDEN && !deleteOnHttp403) {
                /* Login workaround (login detected as super man type) */
                Log.e(TAG, "Trying login workaround after 3s", e)
                delay(3000L)
                return hasValidF1Credentials(deleteOnHttp403 = true)
            }
            f1CredentialsRepository.delete()
            false
        } catch (e: Exception) {
            Log.e(TAG, "Credentials rejected", e)
            f1CredentialsRepository.delete()
            false
        }
    }

    fun getF1Credentials(): F1Credentials =
        f1CredentialsRepository.find()!!

    suspend fun checkAndSave(credentials: F1Credentials): Boolean =
        try {
            Log.d(TAG, credentials.toString())
            val token = f1.authenticate(credentials)
            f1CredentialsRepository.save(credentials)
            f1TokenRepository.save(token)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Credentials rejected", e)
            false
        }

}
