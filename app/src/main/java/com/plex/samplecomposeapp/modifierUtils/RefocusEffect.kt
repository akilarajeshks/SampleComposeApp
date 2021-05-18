package com.plex.samplecomposeapp.modifierUtils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.plex.samplecomposeapp.customcompose.LocalRootViewItem
import com.plex.samplecomposeapp.customcompose.refocus

@Composable
fun RefocusEffect() {
    val rootViewItem = LocalRootViewItem.current

    SideEffect {
        rootViewItem.refocus()
    }
}