package fr.groggy.racecontrol.tv.ui.home

enum class HomeItemType{
    ITEM, ALL
}

data class HomeItem(
    val type: HomeItemType,
    val text: String
)