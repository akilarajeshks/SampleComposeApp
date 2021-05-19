package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import com.plex.samplecomposeapp.customcompose.TVFocusHandler
import com.plex.samplecomposeapp.customcompose.ViewItem
import com.plex.samplecomposeapp.customcompose.refocus

fun Modifier.tvFocusable(viewItem: ViewItem, focusHandler: (() -> TVFocusHandler)? = null): Modifier =
    composed(
        inspectorInfo = debugInspectorInfo {
            name = "makeTvFocusable"
            properties["focusableItem"] = viewItem
        }
    ) {
        val rootViewItem = LocalRootViewItem.current

        SideEffect {
            focusHandler?.invoke()?.let {
                viewItem.focusHandler = it
            }

            rootViewItem.refocus()
        }

        this
    }
