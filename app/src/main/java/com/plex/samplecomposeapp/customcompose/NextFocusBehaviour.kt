package com.plex.samplecomposeapp.customcompose

interface NextFocusBehaviour {
    fun <T : ContainerViewItem> getNext(
        container: T,
        key: ControllerKey
    ): NextFocusState

    object Horizontal : NextFocusBehaviour {
        override fun <T : ContainerViewItem> getNext(container: T, key: ControllerKey): NextFocusState {
            val focusIndex = container.focusIndex
            val lastIndex = container.getLastIndex()

            return if (key == ControllerKey.Left && focusIndex > 0) {
                NextFocusState(focusIndex - 1, true)
            } else if (key === ControllerKey.Right && (lastIndex == null || focusIndex < lastIndex)) {
                NextFocusState(focusIndex + 1, true)
            } else {
                NextFocusState(null, false)
            }
        }
    }

    object Vertical : NextFocusBehaviour {
        override fun <T : ContainerViewItem> getNext(container: T, key: ControllerKey): NextFocusState {
            val focusIndex = container.focusIndex
            val lastIndex = container.getLastIndex()

            return if (key == ControllerKey.Up && focusIndex > 0) {
                NextFocusState(focusIndex - 1, true)
            } else if (key === ControllerKey.Down && (lastIndex == null || focusIndex < lastIndex)) {
                NextFocusState(focusIndex + 1, true)
            } else {
                NextFocusState(null, false)
            }
        }
    }
}
