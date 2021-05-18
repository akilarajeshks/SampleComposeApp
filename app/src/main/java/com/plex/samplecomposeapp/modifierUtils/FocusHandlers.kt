package com.plex.samplecomposeapp.modifierUtils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.plex.samplecomposeapp.cardsDemo.TVListContentPadding
import com.plex.samplecomposeapp.cardsDemo.tvFocusable
import com.plex.samplecomposeapp.customcompose.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val SCROLL_ANIMATION_DURATION = 150

@Suppress("LongParameterList")
fun <T : ContainerViewItem> Modifier.handleTvScroll(
    container: T,
    state: LazyListState,
    contentPadding: TVListContentPadding,
    nextFocus: NextFocusBehaviour,
    scrollBehaviour: ScrollBehaviour,
    coroutineScope: CoroutineScope
): Modifier =
    composed {
        // Make sure our container knows about our lazy list state.
        DisposableEffect(container.viewId) {
            container.lazyListState = state

            onDispose {
                container.lazyListState = null
            }
        }

        handleTvContainerFocus(
            container,
            OnlyWithinLazyListVisibleItems(state, nextFocus),
            onFocusChange = { rootViewItem, focusIndex ->
                coroutineScope.launch {
                    state.scrollAndFocusTV(
                        rootViewItem,
                        container,
                        focusIndex,
                        scrollBehaviour,
                        contentPadding
                    )
                }
            }
        )
    }

@Suppress("LongParameterList")
suspend fun <T : ContainerViewItem> LazyListState.scrollAndFocusTV(
    rootViewItem: RootViewItem,
    container: T,
    focusIndex: Int,
    scrollBehaviour: ScrollBehaviour,
    contentPadding: TVListContentPadding
) {
    stopScroll()

    val foundItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == focusIndex }
        ?: return

    val value = scrollBehaviour.calculateScrollBy(this, foundItem, contentPadding)
    val focusedItem = container.getChild(focusIndex)
    val previousFocusIndex = container.focusIndex

    if (focusedItem == null) {
        //logError { "[scrollAndFocusTV] Failed to focus at index: $focusIndex, focused item is null!" }

        return
    }

    container.focusIndex = focusIndex

    // Don't try to scroll if we failed to focus.  Otherwise scroll runs away and we have focus loss.
    if (!rootViewItem.refocus()) {
        container.focusIndex = previousFocusIndex

        //logError { "[scrollAndFocusTV] Failed to focus at index: $focusIndex, $focusedItem" }
        return
    }

    if (value != 0f) {
        // We want no easing for scroll so it appears smooth when user is pressing and holding on the dpad.
        animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))

        //logDebug { "[scrollAndFocusTV] Scrolled and focused to $focusIndex, $focusedItem" }
    } else {
        //logDebug { "[scrollAndFocusTV] Focused to $focusIndex, $focusedItem" }
    }
}

fun <T : ContainerViewItem> Modifier.handleTvContainerFocus(
    container: T,
    nextFocus: NextFocusBehaviour,
    onFocusChange: ((rootItem: RootViewItem, focusIndex: Int) -> Unit) = { rootItem, focusIndex ->
        container.focusIndex = focusIndex

        rootItem.refocus()
    }
) = then(tvFocusable(container) { ContainerFocusHandler(container, nextFocus, onFocusChange) })

class ContainerFocusHandler(
    val container: ContainerViewItem,
    val nextFocus: NextFocusBehaviour,
    val onFocusChange: (rootItem: RootViewItem, focusIndex: Int) -> Unit
) : TVFocusHandler {
    override fun handleKey(key: ControllerKey, rootViewItem: RootViewItem): Boolean {
        val nextFocusState = nextFocus.getNext(container, key)

        if (nextFocusState.index != null) {
            onFocusChange(rootViewItem, nextFocusState.index)
        }

        return nextFocusState.handleKey
    }

    override fun getFocus(): ViewItem? = container.getChild(container.focusIndex)
}

private class OnlyWithinLazyListVisibleItems(
    val state: LazyListState,
    val nextFocus: NextFocusBehaviour
) : NextFocusBehaviour {
    override fun <T : ContainerViewItem> getNext(container: T, key: ControllerKey): NextFocusState {
        // Get our possible next focus state.
        val focusState = nextFocus.getNext(container, key)

        return if (focusState.index == null) {
            // We don't have a next focus but we still want to do a bit of checking to avoid edge
            // cases with animation not finishing before we focus something else.
            //
            if (state.isScrollInProgress) {
                // We're still animating, handle key to block next focus.
                NextFocusState(null, true)
            } else {
                // Allow next focus state as we are not animating.
                focusState
            }
        } else if (state.layoutInfo.visibleItemsInfo.any { it.index == focusState.index }) {
            // We only allow focus within our realized items otherwise things get confused.
            focusState
        } else {
            // We can't find the focus item in our visible items yet, handle key to block next focus.
            NextFocusState(null, true)
        }
    }
}
