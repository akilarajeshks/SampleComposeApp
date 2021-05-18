package com.plex.samplecomposeapp.customcompose

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun ViewGroup.addOnChildFocusedListener(crossinline onChildFocus: (Boolean, View) -> Unit) {
    val listener = ViewTreeObserver.OnGlobalFocusChangeListener { _, newFocus: View? ->
        newFocus ?: return@OnGlobalFocusChangeListener
        onChildFocus(containsChild(newFocus), newFocus)
    }

    // We add a OnAttachStateChangeListener to make sure we are not listening to focus events when the View is not
    // attached. Since focus is a global thing and the view will not be attached to the UI there is no need to keep
    // listening. This is useful in something like a RecyclerView where views can be detatched during recycling.
    //
    val stateListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
            view.viewTreeObserver.addOnGlobalFocusChangeListener(listener)
        }

        override fun onViewDetachedFromWindow(view: View) {
            view.viewTreeObserver.removeOnGlobalFocusChangeListener(listener)
        }
    }

    addOnAttachStateChangeListener(stateListener)
}
fun ViewGroup?.containsChild(child: View?): Boolean {
    if (child == null || child === this) return false

    var parent = child.parent
    while (parent != null) {
        if (parent === this) return true
        parent = parent.parent
    }

    return false
}
fun Modifier.tvFocusChanged(viewItem: ViewItem, onFocusChanged: (focusState: TVFocusState) -> Unit): Modifier =
    composed {
        DisposableEffect(key1 = viewItem.viewId) {
            viewItem.focusChangedHandlers.add(onFocusChanged)

            // Report our focus state immediately as well.
            viewItem.focusChangedHandlers.forEach { it(viewItem.focusState) }

            onDispose {
                viewItem.focusChangedHandlers.remove(onFocusChanged)
            }
        }

        this
    }
fun Modifier.handleTVKey(viewItem: ViewItem, key: ControllerKey, onKey: () -> Unit) = onTvKey(viewItem, onTvKey = {
        eventKey, _ ->
    if (eventKey == key) {
        onKey()
        return@onTvKey true
    }
    false
})
fun Modifier.onTvKey(
    viewItem: ViewItem,
    onTvKey: ((key: ControllerKey, rootViewItem: RootViewItem) -> Boolean)
): Modifier = composed {
    DisposableEffect(key1 = viewItem.viewId) {
        viewItem.keyHandlers.add(onTvKey)

        onDispose {
            viewItem.keyHandlers.remove(onTvKey)
        }
    }

    this
}

