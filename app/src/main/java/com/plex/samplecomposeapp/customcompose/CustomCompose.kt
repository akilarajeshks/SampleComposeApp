package com.plex.samplecomposeapp.customcompose

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState

private var viewItemIndex = 0L

open class ViewItem {
    val viewId: Long
    internal var focusHandler: TVFocusHandler? = null

    internal var focusState = TVFocusState.None
        set(value) {
            if (value == field)
                return

            field = value
            focusChangedHandlers.forEach { it(value) }
        }

    internal val focusChangedHandlers by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<(TVFocusState) -> Unit>()
    }

    internal val keyHandlers by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<(key: ControllerKey, rootViewItem: RootViewItem) -> Boolean>()
    }

    init {
        viewItemIndex++
        viewId = viewItemIndex
    }
}

interface TVFocusHandler {
    fun handleKey(key: ControllerKey, rootViewItem: RootViewItem): Boolean = false
    fun getFocus(): ViewItem? = null
}

enum class TVFocusState {
    None,
    Active,
    ActiveParent
}
enum class ControllerKey {
    Up, Down, Left, Right, Enter, Back, Menu, Play
}
abstract class ContainerViewItem : ViewItem() {
    init {
        // By default, we'll have a simple focus handler that only returns the current focus.
        focusHandler = object : TVFocusHandler {
            override fun handleKey(key: ControllerKey, rootViewItem: RootViewItem): Boolean = false
            override fun getFocus(): ViewItem? = getChild(focusIndex)
        }
    }

    var focusIndex: Int = 0

    internal var lazyListState: LazyListState? = null

    abstract fun getChild(index: Int): ViewItem?
    abstract fun getLastIndex(): Int?
}
class RootViewItem : ContainerViewItem() {
    // By default we are not focusable. View must explicitly mark to avoid incorrect focusing.
    var isFocusable = false
        set(value) {
            if (field != value) {
                field = value

                if (!value) {
                    // If we are marked as not focusable, clear last known focus path.
                    focusPath = emptyList()
                } else {
                    // Update our focus path if we are focusable again.
                    refocus()
                }
            }
        }

    var focusPath = emptyList<ViewItem>()
        internal set(value) {
            // Only process if our focus path is different.
            if (!field.isDifferentPath(value))
                return

            // Set focus state none if any of the items have dropped out of focus.
            field.forEach { previousItem ->
                if (!value.any { it.viewId == previousItem.viewId })
                    previousItem.focusState = TVFocusState.None
            }

            field = value

            // Update focus states in our path.
            value.forEachIndexed() { index, item ->
                item.focusState = if (index == value.lastIndex) TVFocusState.Active else TVFocusState.ActiveParent
            }
        }

    var children: List<ViewItem> = emptyList()

    override fun getChild(index: Int) = children.getOrNull(index)
    override fun getLastIndex(): Int = children.lastIndex

    override fun toString(): String = "RootViewItem"
}
fun RootViewItem.refocus(): Boolean {
    if (!isFocusable) {
        Log.d( "[refocus] Focus ignored", "root not focusable" )

        return false
    }

    val newFocusPath = getFocusPath()
    val foundChild = newFocusPath.lastOrNull()
    val focusPathText = newFocusPath.joinToString(" -> ") { it.toString() }

    if (foundChild == null) {
        Log.e("[refocus] Focus- !found","path: $focusPathText" )

        return false
    }

    if (!focusPath.isDifferentPath(newFocusPath)) {
        return false
    }

    Log.d( "[refocus] Focus path:", focusPathText)

    focusPath = newFocusPath

    return true
}
fun RootViewItem.getFocusPath(): List<ViewItem> {
    if (!isFocusable) return emptyList()

    val focusPath: MutableList<ViewItem> = mutableListOf(this)
    var focused = focusHandler?.getFocus()

    while (focused != null) {
        focusPath.add(focused)
        focused = focused.focusHandler?.getFocus()
    }

    return focusPath
}
private fun List<ViewItem>.isDifferentPath(other: List<ViewItem>): Boolean {
    if (size != other.size)
        return true

    return withIndex().any {
        it.value.viewId != other[it.index].viewId
    }
}
fun RootViewItem.handleKey(key: ControllerKey): Boolean {
    if (!isFocusable) return false

    val focusPath = getFocusPath().asReversed()
    var handled = false

    for (item in focusPath) {
        handled = item.handleKey(key, this)

        if (handled) {
            break
        }
    }

    return handled
}
private fun ViewItem.handleKey(key: ControllerKey, rootViewItem: RootViewItem): Boolean {
    var handled = focusHandler?.handleKey(key, rootViewItem) ?: false

    if (!handled) {
        for (handler in keyHandlers) {
            handled = handler(key, rootViewItem)

            if (handled) {
                break
            }
        }
    }

    return handled
}
