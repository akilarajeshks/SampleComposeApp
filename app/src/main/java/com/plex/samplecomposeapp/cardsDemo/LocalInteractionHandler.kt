package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.compositionLocalOf
import com.plex.samplecomposeapp.customcompose.Action
import com.plex.samplecomposeapp.customcompose.InteractionHandler

val LocalInteractionHandler = compositionLocalOf<InteractionHandler> { NoOpInteractionHandler }
private object NoOpInteractionHandler : InteractionHandler {
    override fun onAction(action: Action) {}
}