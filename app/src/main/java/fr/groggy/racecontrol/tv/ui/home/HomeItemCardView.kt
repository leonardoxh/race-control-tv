package fr.groggy.racecontrol.tv.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import fr.groggy.racecontrol.tv.R

class HomeItemCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.style.TextCardStyle
) : BaseCardView(
    context,
    attrs,
    defStyle
) {
    private val textView: TextView

    init {
        inflate(context, R.layout.home_item_card, this)
        isFocusable = true

        textView = findViewById(R.id.text_view)
    }

    fun setText(text: CharSequence?) {
        textView.text = text
    }
}