package org.akaii.kettles8.emulator.input

import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

class FutureInput {
    companion object {
        private sealed interface FutureInputState {
            data class Pending(val onKeyReady: (Key) -> Unit) : FutureInputState
            data object Inactive : FutureInputState
        }
    }

    private var waitOnInput: FutureInputState = FutureInputState.Inactive

    fun onNextKeyReady(onKeyReady: (Key) -> Unit) {
        waitOnInput = FutureInputState.Pending(onKeyReady)
    }

    fun isPending(): Boolean = waitOnInput is FutureInputState.Pending

    fun checkInput(keypad: Keypad) {
        when(val state = waitOnInput) {
            is FutureInputState.Inactive -> Unit
            is FutureInputState.Pending -> keypad.keyReleased()?.let {
                state.onKeyReady(it)
                waitOnInput = FutureInputState.Inactive
            }
        }
    }
}

