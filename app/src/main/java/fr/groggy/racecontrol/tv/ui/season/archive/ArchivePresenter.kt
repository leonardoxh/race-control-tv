package fr.groggy.racecontrol.tv.ui.season.archive

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import fr.groggy.racecontrol.tv.R
import fr.groggy.racecontrol.tv.f1tv.Archive

class ArchivePresenter: Presenter() {
    companion object {
        private val TAG = ArchivePresenter::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_archive_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val view = viewHolder?.view as TextView
        val archiveItem = item as Archive
        view.text = archiveItem.year.toString()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val view = viewHolder?.view as TextView
        view.text = null
    }
}
