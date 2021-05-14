package fr.groggy.racecontrol.tv.ui.session

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_CONTENT
import androidx.leanback.widget.ImageCardView.CARD_TYPE_FLAG_TITLE
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import fr.groggy.racecontrol.tv.R

class SessionCardPresenter : Presenter() {

    companion object {
        private const val WIDTH = 313
        private const val HEIGHT = 176
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_card, parent, false)

        val imageCardView = view.findViewById<ImageCardView>(R.id.image_card_view)
        imageCardView.setMainImageDimensions(
            WIDTH,
            HEIGHT
        )
        imageCardView.cardType = CARD_TYPE_FLAG_TITLE or CARD_TYPE_FLAG_CONTENT

        imageCardView.findViewById<TextView>(R.id.title_text)?.setLines(2)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val imageCardView = viewHolder.view.findViewById<ImageCardView>(R.id.image_card_view)
        val session = item as SessionCard

        imageCardView.titleText = session.name
        imageCardView.contentText = session.contentSubtype

        Glide.with(viewHolder.view.context)
            .load(session.thumbnail?.url)
            .into(imageCardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val imageCardView = viewHolder.view.findViewById<ImageCardView>(R.id.image_card_view)
        imageCardView.badgeImage = null
        imageCardView.mainImage = null
    }

}

interface SessionCard {

    val name: String
    val contentSubtype: String
    val thumbnail: Image?

    interface Image {
        val url: Uri
    }

}
