package fr.groggy.racecontrol.tv.ui.player

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import fr.groggy.racecontrol.tv.R
import kotlin.math.roundToInt

class ResolutionSelectionDialog(
    trackInfo: MappingTrackSelector.MappedTrackInfo
): DialogFragment() {
    private var onResolutionSelectedListener: ((Int, Int) -> Unit)? = null

    private val formats: List<Format> by lazy {
        val trackGroups = trackInfo.getTrackGroups(C.TRACK_TYPE_DEFAULT)
        val formats = mutableListOf<Format>()
        for (i in 0 until trackGroups.length) {
            val trackGroup = trackGroups[i]
            for (j in 0 until trackGroup.length) {
                if (trackInfo.getTrackSupport(C.TRACK_TYPE_DEFAULT, i, j) == C.FORMAT_HANDLED) {
                    val format = trackGroup.getFormat(j)
                    if (format.frameRate > 1F) {
                        formats.add(format)
                    }
                }
            }
        }
        formats
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = listOf(getText(R.string.video_selection_quality_auto)) + formats.map {
            requireContext().getString(R.string.video_quality, it.height, it.frameRate.roundToInt())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.video_selection_dialog_title)
            .setItems(items.toTypedArray()) { _, i -> selectVideo(i) }
            .create()
    }

    fun setResolutionSelectedListener(resolutionSelectedListener: (Int, Int) -> Unit): ResolutionSelectionDialog {
        onResolutionSelectedListener = resolutionSelectedListener
        return this
    }

    private fun selectVideo(index: Int) {
        if (index == 0) {
            onResolutionSelectedListener?.invoke(Int.MAX_VALUE, Int.MAX_VALUE)
            return
        }
        val format = formats[index - 1]
        onResolutionSelectedListener?.invoke(format.width, format.height)
    }
}
