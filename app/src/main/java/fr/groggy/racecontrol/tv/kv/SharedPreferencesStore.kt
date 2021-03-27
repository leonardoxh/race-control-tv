package fr.groggy.racecontrol.tv.kv

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences("fr.groggy.racecontrol.tv.DATA_KEY_VALUE_STORE", MODE_PRIVATE)
    }

    fun findString(key: String): String? =
        sharedPreferences.getString(key, null)

    fun update(f: SharedPreferences.Editor.() -> Unit): Unit =
        with(sharedPreferences.edit()) {
            f()
            apply()
        }

}
