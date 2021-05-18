package com.plex.samplecomposeapp.cardsDemo

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plex.samplecomposeapp.customcompose.CardViewItem

@Composable
fun CardsDemoScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(20) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(180.dp)
                        .width(100.dp)
                ) {
                    val card = CardViewItem(
                        title = "Custom card title",
                        "Subtitle ",
                        imageUrl = "https://artworks.thetvdb.com/banners/movies/31/posters/31.jpg"
                    )
                    CardView(cardItem = card)
                }
                Text("Card Item")
            }
        }
    }
}
