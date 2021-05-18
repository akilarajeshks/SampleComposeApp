package com.plex.samplecomposeapp.cardsDemo

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.plex.samplecomposeapp.customcompose.*
import com.plex.samplecomposeapp.modifierUtils.whenTV

@Composable
fun CardView(cardItem: CardViewItem, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val cardScale = animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.10f
    ).value

    val currentFocus = LocalCurrentFocusedItem.current
    val interactionHandler = LocalInteractionHandler.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .scale(cardScale),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            backgroundColor = Color.Black,
            shape = MaterialTheme.shapes.medium,
            elevation = 5.dp,
            border = if (isFocused) BorderStroke(1.dp, Color.White) else null,
            modifier = Modifier
                .clickable(onClick = {
                    interactionHandler.onAction(CardAction(cardItem))
                })
                .whenTV({
                    tvFocusChanged(cardItem) {
                        it
                        Log.d( "[CardView] ","onFocusChanged: $it, ${cardItem.title}")

                        isFocused = it == TVFocusState.Active

                        if (isFocused)
                            currentFocus.currentCard.value = cardItem
                    }
                        .tvFocusable(cardItem)
                        .handleTVKey(cardItem, ControllerKey.Enter, onKey = {
                            interactionHandler.onAction(CardAction(cardItem))
                        })

                }, applicationContext = null)
        ) {
            CardContent(cardItem)
        }
    }
}

@Composable
private fun CardContent(item: CardViewItem, modifier: Modifier = Modifier) {
    val cardModifier = modifier
        .width(100.dp)
        .height(180.dp)

    Box(modifier = cardModifier) {
        Image(
            painter = rememberCoilPainter(request = item.imageUrl),
            contentDescription = "Cardimage",
            modifier = Modifier.fillMaxSize()
        )
    }
}
