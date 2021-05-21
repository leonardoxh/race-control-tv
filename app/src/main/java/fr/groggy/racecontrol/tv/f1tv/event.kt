package fr.groggy.racecontrol.tv.f1tv

import fr.groggy.racecontrol.tv.core.InstantPeriod

class F1TvEventId(val value: String)

data class F1TvEvent(
    val id: F1TvEventId,
    val name: String,
    val period: InstantPeriod
)
