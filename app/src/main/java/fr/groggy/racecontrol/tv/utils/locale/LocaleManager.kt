package fr.groggy.racecontrol.tv.utils.locale

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocaleManager @Inject constructor() {

    companion object {
        private val TAG = LocaleManager::class.simpleName
    }

    // Currently support by F1 API: ENG, DEU, FRA, NLD, SPA, PRT
    var currentLocale: String = "ENG"
        set(iso3Language) {
            Log.d(TAG, iso3Language)
            field = when (iso3Language) {
                "deu" -> "DEU"
                "fra" -> "FRA"
                "nld" -> "NLD"
                "spa" -> "SPA"
                "por" -> "PRT"
                else -> "ENG"
            }
        }

    fun getISO3LanguageFromConfiguration(configuration: Configuration): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales.get(0).isO3Language
        } else {
            configuration.locale.isO3Language
        }
    }
}