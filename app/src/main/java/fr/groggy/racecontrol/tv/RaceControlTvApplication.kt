package fr.groggy.racecontrol.tv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaceControlTvApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        AndroidThreeTen.init(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
}
