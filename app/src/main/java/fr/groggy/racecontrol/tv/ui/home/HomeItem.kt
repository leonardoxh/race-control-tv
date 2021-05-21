package fr.groggy.racecontrol.tv.ui.home

enum class HomeItemType {
    ARCHIVE, ARCHIVE_ALL
}

data class HomeItem(
    val type: HomeItemType,
    val text: String
)