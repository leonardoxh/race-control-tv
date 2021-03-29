package fr.groggy.racecontrol.tv.ui.player

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.RendererCapabilities
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.TrackGroupArray
import fr.groggy.racecontrol.tv.R
import kotlin.math.roundToInt

class ResolutionSelectionDialog(
    trackGroups: TrackGroupArray
): DialogFragment() {
    private val formats by lazy {
        val formats = mutableListOf<Format>()
        for (i in 0 until trackGroups.length) {
            val trackGroup = trackGroups[i]
            for (j in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(j)
                formats.add(format)
            }
        }
        formats.toList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = formats.map { requireContext().getString(R.string.video_quality, it.height, it.frameRate.roundToInt()) }.toTypedArray()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.video_selection_dialog_title)
            .setItems(items) { _, i -> selectVideo(i) }
            .create()
    }

    private fun selectVideo(index: Int) {

    }
}
