package fr.groggy.racecontrol.tv.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import fr.groggy.racecontrol.tv.R

class HomeItemPresenter: Presenter() {
    companion object {
        private val TAG = HomeItemPresenter::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val view = viewHolder.view as HomeItemCardView
        val homeItem = item as HomeItem
        view.setText(homeItem.text)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val view = viewHolder?.view as HomeItemCardView
        view.setText(null)
    }
}