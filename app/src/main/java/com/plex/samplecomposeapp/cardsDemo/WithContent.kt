package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
 import com.plex.samplecomposeapp.customcompose.RootViewItem
import com.plex.samplecomposeapp.customcompose.ViewItem
import com.plex.samplecomposeapp.customcompose.refocus

@Composable
fun <T> WithContent(
    data: T,
    updater: () -> Unit,
    content: @Composable
        (children: T) -> Unit
) {
    val rootViewItem = LocalRootViewItem.current
    SideEffect {
        updater()
        rootViewItem.refocus()
    }
    content(data)
}

@Composable
fun <T : ViewItem> WithContent(
    parent: RootViewItem,
    child: T,
    content: @Composable
        (child: T) -> Unit
) = WithContent(child, {
    parent.children = listOf(child)
}, content)
