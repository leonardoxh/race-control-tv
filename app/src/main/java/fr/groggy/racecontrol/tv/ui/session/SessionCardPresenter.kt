package fr.groggy.racecontrol.tv.ui.session

import android.content.Context
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
import java.util.*

class SessionCardPresenter: Presenter() {

    private lateinit var context: Context

    companion object {
        private val TAG = SessionCardPresenter::class.simpleName
        private const val WIDTH = 313
        private const val HEIGHT = 176
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        context = parent.context
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
        val view = viewHolder.view as ImageCardView
        val session = item as SessionCard

        val contentSubtype = "session_content_type_" + session.contentSubtype.toLowerCase(Locale.ROOT).replace(" ", "_")
        val contentSubtypeTranslated: String = try {
            context.resources.getString(context.resources.getIdentifier(contentSubtype, "string", context.packageName))
        } catch (e: Exception) {
            Log.d(TAG, "New translation for $contentSubtype needed")
            session.contentSubtype
        }

        view.titleText = session.name
        view.contentText = contentSubtypeTranslated.toUpperCase(Locale.ROOT)

        Glide.with(viewHolder.view.context)
            .load(session.thumbnail?.url)
            .into(view.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val view = viewHolder.view as ImageCardView
        view.badgeImage = null
        view.mainImage = null
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
