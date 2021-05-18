package com.plex.samplecomposeapp.customcompose

import android.content.Context
import android.graphics.Rect
import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.AbstractComposeView
import com.plex.samplecomposeapp.modifierUtils.whenTV

class CustomComposeView(
    context: Context,
    private val rootViewItem: RootViewItem,
    private val interactionHandler: InteractionHandler,
    private val content: @Composable
        () -> Unit
) : AbstractComposeView(context, null, 0) {
    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        this.addOnChildFocusedListener { hasFocus, _ ->
            rootViewItem.isFocusable = hasFocus

            if (rootViewItem.isFocusable) {
                focusRequester?.requestFocus()
            }
        }
    }

    private var focusRequester: FocusRequester? = null

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)

        if (gainFocus) {
            rootViewItem.isFocusable = true
            focusRequester?.requestFocus()
        }
    }
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalRootViewItem provides rootViewItem,
            LocalInteractionHandler provides interactionHandler
        ) {
            val requester = remember { FocusRequester() }

            Box(modifier = Modifier.whenTV ({
                focusRequester(requester)
                    .focusModifier()
                    .onFocusChanged {
                        if (it.isFocused) {
                            rootViewItem.refocus()
                        }
                    }
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyUp) return@onKeyEvent false

                        val key =
                            toTvKey(keyEvent.nativeKeyEvent.keyCode) ?: return@onKeyEvent false

                        rootViewItem.handleKey(key)
                    }
            },applicationContext = context)) {
                content()
            }

            SideEffect {
                focusRequester = requester

                if (rootViewItem.isFocusable) {
                    requester.requestFocus()
                }
            }
        }
    }
}
fun toTvKey(keyCode: Int): ControllerKey? = when (keyCode) {
    KeyEvent.KEYCODE_DPAD_UP -> ControllerKey.Up
    KeyEvent.KEYCODE_DPAD_DOWN -> ControllerKey.Down
    KeyEvent.KEYCODE_DPAD_LEFT -> ControllerKey.Left
    KeyEvent.KEYCODE_DPAD_RIGHT -> ControllerKey.Right
    KeyEvent.KEYCODE_DPAD_CENTER,
    KeyEvent.KEYCODE_BUTTON_SELECT,
    KeyEvent.KEYCODE_ENTER,
    KeyEvent.KEYCODE_BUTTON_A -> ControllerKey.Enter
    KeyEvent.KEYCODE_BACK -> ControllerKey.Back
    KeyEvent.KEYCODE_MEDIA_PLAY -> ControllerKey.Play
    else -> null
}

private object NoOpInteractionHandler : InteractionHandler {
    override fun onAction(action: Action) {}
}

val LocalRootViewItem = compositionLocalOf { RootViewItem() }
val LocalInteractionHandler = compositionLocalOf<InteractionHandler> { NoOpInteractionHandler }