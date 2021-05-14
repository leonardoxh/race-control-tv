package fr.groggy.racecontrol.tv.ui.common

import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRowPresenter

class CustomListRowPresenter : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE) {
    init {
        shadowEnabled = false
        selectEffectEnabled = false
    }
}