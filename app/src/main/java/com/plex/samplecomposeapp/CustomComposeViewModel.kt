package com.plex.samplecomposeapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.plex.samplecomposeapp.cardsDemo.CardsDemoScreen
import com.plex.samplecomposeapp.cardsDemo.NavigationListScreen
import com.plex.samplecomposeapp.customcompose.RootViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class CustomComposeViewModel : ViewModel(), ComposeViewModel {
    override val rootViewItem: RootViewItem = RootViewItem()

    private val _currentScreen = MutableStateFlow<Screen>(Main)
    private val navStack = LinkedList(mutableListOf(_currentScreen.value))
    val currentScreenObservable: Flow<Screen> get() = _currentScreen

    fun navigate(screen: Screen) {
        navStack.add(screen)
        _currentScreen.value = screen
    }

}

//Add all screens here.
sealed class Screen(
    val compose: @Composable
        (CustomComposeViewModel) -> Unit
)

object Main : Screen({
    NavigationListScreen(screens = listOf(Cards, Cards), customComposeViewModel = it)
})


// view level ui
object Cards : Screen({ CardsDemoScreen() })
