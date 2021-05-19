package com.plex.samplecomposeapp.cardsDemo

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plex.samplecomposeapp.CustomComposeViewModel
import com.plex.samplecomposeapp.Screen
import com.plex.samplecomposeapp.customcompose.OptionViewAction
import com.plex.samplecomposeapp.customcompose.SingleColumnTextCell
import com.plex.samplecomposeapp.customcompose.Unknown
import com.plex.samplecomposeapp.modifierUtils.RefocusEffect
import com.plex.samplecomposeapp.modifierUtils.isTVDevice

@Composable
fun NavigationListScreen(
    screens: List<Screen>,
    customComposeViewModel: CustomComposeViewModel
) {
    val data = remember {
        val width = Resources.getSystem().displayMetrics.widthPixels

        OptionContainerViewItem().apply {
            children = screens.map {
                OptionViewItem(
                    width = width.dp, height = 100.dp, title = "List item",
                    wrappedData = Unknown(it)
                )
            }
        }
    }

    val actionHandler = LocalInteractionHandler.current

    WithContent(customComposeViewModel.rootViewItem, data) {
        if (isTVDevice()) {
            TVScrollingLazyList(
                container = data,
                isVertical = true,
                spaceBetween = 12.dp,
                contentPadding = TVListContentPadding(12.dp)
            ) {
                items(data.children.count()) {
                    val item = data.children[it]
                    SingleColumnTextCell(item,
                        onSelect =
                        {

                            actionHandler.onAction(OptionViewAction(item))
                        }
                    )

                    if (it == data.focusIndex) {
                        RefocusEffect()
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                items(data.children.count()) {
                    val item = data.children[it]
                    SingleColumnTextCell(item,
                        onSelect =
                        {
                            actionHandler.onAction(OptionViewAction(item))
                        }
                    )
                }
            }
        }
    }
}
