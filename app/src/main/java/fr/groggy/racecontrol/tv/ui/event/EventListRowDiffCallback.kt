package fr.groggy.racecontrol.tv.ui.event

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ListRow
import fr.groggy.racecontrol.tv.ui.session.SessionCard

class EventListRowDiffCallback: DiffCallback<ListRow>() {

    override fun areItemsTheSame(oldItem: ListRow, newItem: ListRow): Boolean =
        oldItem.headerItem.name == newItem.headerItem.name

    override fun areContentsTheSame(oldItem: ListRow, newItem: ListRow): Boolean {
        val oldContent = (oldItem.adapter as ArrayObjectAdapter).unmodifiableList<SessionCard>()
        val newContent = (newItem.adapter as ArrayObjectAdapter).unmodifiableList<SessionCard>()
        return oldContent == newContent
    }

}
