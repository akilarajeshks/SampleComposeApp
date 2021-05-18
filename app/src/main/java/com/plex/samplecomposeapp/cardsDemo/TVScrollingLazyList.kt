package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plex.samplecomposeapp.customcompose.ContainerViewItem
import com.plex.samplecomposeapp.customcompose.NextFocusBehaviour
import com.plex.samplecomposeapp.customcompose.ScrollBehaviour
import com.plex.samplecomposeapp.modifierUtils.handleTvScroll

@Composable
fun TVScrollingLazyList(
    container: ContainerViewItem,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: TVListContentPadding = TVListContentPadding(),
    spaceBetween: Dp = 0.dp,
    state: LazyListState = rememberLazyListState(),
    scrollBehaviour: ScrollBehaviour = ScrollBehaviour.ContainedWithinViewport,
    content: LazyListScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val listModifier = modifier
        .handleTvScroll(
            container,
            state,
            contentPadding,
            if (isVertical) NextFocusBehaviour.Vertical else NextFocusBehaviour.Horizontal,
            scrollBehaviour,
            coroutineScope
        )

    val listContentPadding = if (isVertical)
        PaddingValues(
            top = contentPadding.start,
            bottom = contentPadding.end
        )
    else {
        PaddingValues(
            start = contentPadding.start,
            end = contentPadding.end
        )
    }

    if (isVertical) {
        LazyColumn(
            state = state,
            contentPadding = listContentPadding,
            verticalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Top),
            modifier = listModifier,
            content = content
        )
    } else {
        LazyRow(
            state = state,
            contentPadding = listContentPadding,
            horizontalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Start),
            modifier = listModifier,
            content = content
        )
    }
}

data class TVListContentPadding(val start: Dp = 0.dp, val end: Dp = 0.dp) {
    constructor(padding: Dp = 0.dp) : this(padding, padding)
}