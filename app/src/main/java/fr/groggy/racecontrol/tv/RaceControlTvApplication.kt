package fr.groggy.racecontrol.tv

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import fr.groggy.racecontrol.tv.utils.locale.LocaleManager
import javax.inject.Inject

@HiltAndroidApp
class RaceControlTvApplication: Application() {
    @Inject
    lateinit var localeManager: LocaleManager

    override fun onCreate() {
        super.onCreate()
        localeManager.currentLocale = localeManager.getISO3LanguageFromConfiguration(Resources.getSystem().configuration)

        AndroidThreeTen.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager.currentLocale = localeManager.getISO3LanguageFromConfiguration(newConfig)
    }
}
