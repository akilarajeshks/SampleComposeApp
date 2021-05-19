package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.compositionLocalOf
import com.plex.samplecomposeapp.customcompose.Action
import com.plex.samplecomposeapp.customcompose.InteractionHandler
import com.plex.samplecomposeapp.customcompose.RootViewItem

private object NoOpInteractionHandler : InteractionHandler {
    override fun onAction(action: Action) {}
}

val LocalRootViewItem = compositionLocalOf { RootViewItem() }
val LocalInteractionHandler = compositionLocalOf<InteractionHandler> { NoOpInteractionHandler }