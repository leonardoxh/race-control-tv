package fr.groggy.racecontrol.tv.ui.channel

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_CONTENT
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_TITLE
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType.Companion.Data
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType.Companion.PitLane
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType.Companion.Tracker
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType.Companion.Unknown
import fr.groggy.racecontrol.tv.f1tv.F1TvBasicChannelType.Companion.Wif

class ChannelCardPresenter: Presenter() {

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
        view.cardType = CARD_TYPE_FLAG_TITLE or CARD_TYPE_FLAG_CONTENT
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        Log.d(TAG, "onBindViewHolder")
        val view = viewHolder.view as ImageCardView
        when(item) {
            is BasicChannelCard -> {
                val type = item.type
                view.titleText = when(type) {
                    Wif -> "Main broadcast"
                    PitLane -> "Pit lane"
                    Tracker -> "Tracker"
                    Data -> "Data"
                    is Unknown -> type.name
                }
                view.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, android.R.color.black))
                view.contentText = null
            }
            is OnboardChannelCard -> {
                view.titleText = item.name
                view.contentText = item.subTitle
                if (item.background != null) {
                    view.setBackgroundColor(Color.parseColor(item.background))
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, android.R.color.black))
                }
                item.driver?.headshot?.let {
                    Glide.with(viewHolder.view.context)
                        .load(it.url)
                        .centerCrop()
                        .into(view.mainImageView)
                }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val view = viewHolder.view as ImageCardView
        view.titleText = null
        view.contentText = null
        view.badgeImage = null
        view.mainImage = null
    }

}

interface BasicChannelCard {
    val type: F1TvBasicChannelType
}

interface OnboardChannelCard {

    interface Driver {
        val racingNumber: Int
        val headshot: Image?
    }

    interface Image {
        val url: Uri
    }

    val name: String
    val driver: Driver?
    val background: String?
    val subTitle: String?
}
