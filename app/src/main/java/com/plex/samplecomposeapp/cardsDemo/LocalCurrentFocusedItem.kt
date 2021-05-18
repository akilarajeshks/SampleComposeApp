package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.staticCompositionLocalOf
import com.plex.samplecomposeapp.customcompose.CardViewItem
import kotlinx.coroutines.flow.MutableStateFlow

val LocalCurrentFocusedItem = staticCompositionLocalOf<CurrentFocus> {
    object : CurrentFocus {
        override val currentCard = MutableStateFlow<CardViewItem?>(null)
    }
}

interface CurrentFocus {
    val currentCard: MutableStateFlow<CardViewItem?>
    fun reset() {
        currentCard.value = null
    }
}
