package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import com.plex.samplecomposeapp.customcompose.ContainerViewItem
import com.plex.samplecomposeapp.customcompose.Unknown
import com.plex.samplecomposeapp.customcompose.ViewItem

class OptionContainerViewItem : ContainerViewItem() {
    var children: List<OptionViewItem> = emptyList()

    override fun getChild(index: Int) = children.getOrNull(index)
    override fun getLastIndex(): Int = children.size - 1

    override fun toString(): String = "OptionContainerViewItem"
}

class OptionViewItem(
    val title: String,
    val width: Dp = Dp.Unspecified,
    val height: Dp = Dp.Unspecified,
    val tag: String? = null,
    val wrappedData: Unknown = Unknown(null)
) : ViewItem() {
    private var isSelectedState = mutableStateOf(false)

    var isSelected
        get() = isSelectedState.value
        set(value) {
            isSelectedState.value = value
        }

    override fun toString() = "OptionViewItem: $title"
}