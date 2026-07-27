package com.guga.asunaanimes.presentation.common

import androidx.annotation.StringRes

/** One-shot feedback the UI should surface to the user, typically as a toast. */
data class UiMessage(@StringRes val textResId: Int)
