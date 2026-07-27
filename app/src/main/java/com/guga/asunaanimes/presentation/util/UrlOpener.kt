package com.guga.asunaanimes.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.guga.asunaanimes.R

private const val FALLBACK_URL = "https://myanimelist.net/"

/**
 * Opens [url] in the user's browser. Devices without a browser used to crash the app with an
 * [android.content.ActivityNotFoundException], so the failure is now reported to the user instead.
 */
fun Context.openInBrowser(url: String?) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url ?: FALLBACK_URL))
    runCatching { startActivity(intent) }.onFailure {
        Toast.makeText(this, R.string.message_cannot_open_link, Toast.LENGTH_SHORT).show()
    }
}
