package com.plex.samplecomposeapp.customcompose

import android.content.res.Resources
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import com.plex.samplecomposeapp.cardsDemo.TVListContentPadding

sealed class ScrollBehaviour {
    abstract fun calculateScrollBy(
        lazyListState: LazyListState,
        focusItem: LazyListItemInfo,
        contentPadding: TVListContentPadding
    ): Float
    class AdditionalOffset(val behaviour: ScrollBehaviour, val offset: Dp) : ScrollBehaviour() {
        override fun calculateScrollBy(
            lazyListState: LazyListState,
            focusItem: LazyListItemInfo,
            contentPadding: TVListContentPadding
        ): Float {
            val value = behaviour.calculateScrollBy(lazyListState, focusItem, contentPadding)
            val density = Resources.getSystem().displayMetrics.density

            return value + (offset.value * density)
        }
    }

    class Selector(val selector: (focusIndex: Int) -> ScrollBehaviour) : ScrollBehaviour() {
        override fun calculateScrollBy(
            lazyListState: LazyListState,
            focusItem: LazyListItemInfo,
            contentPadding: TVListContentPadding
        ): Float {
            return selector(focusItem.index).calculateScrollBy(
                lazyListState, focusItem, contentPadding
            )
        }
    }

    object ContainedWithinViewport : ScrollBehaviour() {
        override fun calculateScrollBy(
            lazyListState: LazyListState,
            focusItem: LazyListItemInfo,
            contentPadding: TVListContentPadding
        ): Float {
            val density = Resources.getSystem().displayMetrics.density

            // Our item is in our viewport so we can accurately scroll it within our "actual" viewport
            val actualViewportStart = lazyListState.layoutInfo.viewportStartOffset
            val actualViewportEnd = lazyListState.layoutInfo.viewportEndOffset
            val actualViewportSize = actualViewportEnd - actualViewportStart

            val itemStart = focusItem.offset
            val itemEnd = focusItem.offset + focusItem.size

            val contentStartPaddingPixels = contentPadding.start.value * density
            val contentEndPaddingPixels = contentPadding.end.value * density

            return when {
                itemStart < actualViewportStart + contentStartPaddingPixels -> {
                    // Item is before viewport.
                    itemStart.toFloat()
                }
                itemEnd > actualViewportStart + actualViewportSize - contentEndPaddingPixels -> {
                    // Item is after viewport.
                    itemEnd - actualViewportSize + contentEndPaddingPixels - actualViewportStart
                }
                else -> {
                    // Item is in viewport, no change to scroll position.
                    0f
                }
            }
        }
    }

    object TopWithinViewport : ScrollBehaviour() {
        override fun calculateScrollBy(
            lazyListState: LazyListState,
            focusItem: LazyListItemInfo,
            contentPadding: TVListContentPadding
        ): Float {
            return focusItem.offset.toFloat()
        }
    }

    object CenteredWithinViewport : ScrollBehaviour() {
        override fun calculateScrollBy(
            lazyListState: LazyListState,
            focusItem: LazyListItemInfo,
            contentPadding: TVListContentPadding
        ): Float {
            // Shortcut if we are the first item, we always want it top
            if (focusItem.index == 0) {
                return focusItem.offset.toFloat()
            }

            val density = Resources.getSystem().displayMetrics.density

            // Our item is in our viewport so we can accurately scroll it within our "actual" viewport
            val contentStartPaddingPixels = contentPadding.start.value * density
            val contentEndPaddingPixels = contentPadding.end.value * density

            val actualViewportStart = lazyListState.layoutInfo.viewportStartOffset + contentStartPaddingPixels
            val actualViewportEnd = lazyListState.layoutInfo.viewportEndOffset - contentEndPaddingPixels
            val actualViewportSize = actualViewportEnd - actualViewportStart

            val value = focusItem.offset - (actualViewportSize / 2f - focusItem.size / 2f)

            val firstItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()

            // If we are at the front of the list we can guard against over scrolling.
            return if (firstItem?.index == 0) {
                maxOf(value, firstItem.offset.toFloat())
            } else {
                value
            }
        }
    }
}
