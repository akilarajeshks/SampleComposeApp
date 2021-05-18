package com.plex.samplecomposeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.plex.samplecomposeapp.customcompose.*

class MainActivity : AppCompatActivity() {
    private lateinit var customComposeViewModel: CustomComposeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        customComposeViewModel = ViewModelProvider(this)[CustomComposeViewModel::class.java]
        val coordinator = object : InteractionHandler {
            override fun onAction(action: Action) {
                when(action) {
                    is CardAction -> (action.model.wrappedData.value as Screen).let(customComposeViewModel::navigate)
                    is OptionViewAction -> (action.model.wrappedData.value as Screen).let(customComposeViewModel::navigate)
                }
            }
        }

        val customComposeView = CustomComposeView(
            context = applicationContext,
            rootViewItem = customComposeViewModel.rootViewItem,
            interactionHandler = coordinator
            ) {
            val currentView =
                customComposeViewModel.currentScreenObservable.collectAsState(initial = Main)
            Column(modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()) {
                if (currentView.value is Main) {
                    Box(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                        Text(
                            text = "Main",
                            color = colors.primary,
                            style = MaterialTheme.typography.h2,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                currentView.value.compose(customComposeViewModel)
            }
        }
        setContentView(customComposeView)
    }
}