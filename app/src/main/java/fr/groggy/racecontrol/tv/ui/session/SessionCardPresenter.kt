package fr.groggy.racecontrol.tv.ui.session

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_CONTENT
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_TITLE
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import fr.groggy.racecontrol.tv.R

class SessionCardPresenter: Presenter() {

    companion object {
        private val TAG = SessionCardPresenter::class.simpleName

        private const val WIDTH = 313
        private const val HEIGHT = 176
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = ImageCardView(parent.context)
        view.setMainImageDimensions(
            WIDTH,
            HEIGHT
        )
        view.cardType = CARD_TYPE_FLAG_TITLE or CARD_TYPE_FLAG_CONTENT

        view.findViewById<TextView>(R.id.title_text)?.setLines(2)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        Log.d(TAG, "onBindViewHolder")
        val view = viewHolder.view as ImageCardView
        val session = item as SessionCard

        view.titleText = session.name
        view.contentText = if (session.live) {
            viewHolder.view.context.getText(R.string.live)
        } else {
            viewHolder.view.context.getText(R.string.replay)
        }

        Glide.with(viewHolder.view.context)
            .load(session.thumbnail?.url)
            .into(view.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val view = viewHolder.view as ImageCardView
        view.badgeImage = null
        view.mainImage = null
    }

}

interface SessionCard {

    val name: String
    val live: Boolean
    val thumbnail: Image?

    interface Image {
        val url: Uri
    }

}
