package com.plex.samplecomposeapp.customcompose

import com.plex.samplecomposeapp.cardsDemo.OptionViewItem

interface InteractionHandler {
    fun onAction(action: Action)
}

sealed class Action
data class CardAction(val model: CardViewItem) : Action()
data class OptionViewAction(val model: OptionViewItem) : Action()


class CardViewItem(
    val title : String,
    val subtitle : String,
    val imageUrl : String,
    val wrappedData : Unknown = Unknown(null)
) : ViewItem(){
    override fun toString(): String {
        return "CardViewItem : $title, $subtitle"
    }
}

inline class Unknown(val value: Any?)
