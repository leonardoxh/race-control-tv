package fr.groggy.racecontrol.tv.ui.season.archive

import android.util.Log
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import fr.groggy.racecontrol.tv.f1tv.Archive
import fr.groggy.racecontrol.tv.ui.channel.ChannelCardPresenter

class ArchivePresenter: Presenter() {
    companion object {
        private val TAG = ChannelCardPresenter::class.simpleName

        private const val WIDTH = 313
        private const val HEIGHT = 274
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = ImageCardView(parent.context)
        view.setMainImageDimensions(
            WIDTH,
            HEIGHT
        )
        view.cardType = ImageCardView.CARD_TYPE_FLAG_TITLE
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val view = viewHolder?.view as ImageCardView
        val archiveItem = item as Archive
        view.titleText = archiveItem.year.toString()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val view = viewHolder?.view as ImageCardView
        view.titleText = null
    }
}
