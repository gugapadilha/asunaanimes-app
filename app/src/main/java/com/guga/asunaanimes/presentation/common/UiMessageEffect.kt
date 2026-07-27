package com.guga.asunaanimes.presentation.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Collects one-shot messages only while the screen is started, so a toast is never triggered by a
 * screen the user already left.
 */
@Composable
fun UiMessageEffect(messages: Flow<UiMessage>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(messages, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            messages.collect { message ->
                Toast.makeText(context, message.textResId, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
