package com.plex.samplecomposeapp.customcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.plex.samplecomposeapp.cardsDemo.OptionViewItem
import com.plex.samplecomposeapp.cardsDemo.tvFocusable
import com.plex.samplecomposeapp.modifierUtils.whenTV
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SingleColumnTextCell(
    cellItem: OptionViewItem,
    onSelect: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .width(cellItem.width)
            .height(cellItem.height)
            .whenTV ({
                tvFocusChanged(cellItem) {
                    isFocused = it == TVFocusState.Active
                }
            },null)
            .clickable { onSelect() }
            .background(
                color = if (isFocused) Color.Gray else Color.Black,
                shape = MaterialTheme.shapes.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.whenTV ({
                tvFocusable(cellItem).handleTVKey(cellItem, ControllerKey.Enter) { onSelect() }
            },null),
            text = cellItem.title,
            style = MaterialTheme.typography.caption,
            color = if (isFocused) Color.Gray else colors.primary
        )
    }
}
