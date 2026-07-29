package com.guga.asunaanimes.core.locale

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.guga.asunaanimes.domain.model.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Applies the selected [AppLanguage] through AndroidX per-app locales so Compose
 * `stringResource` values refresh after the activity recreates.
 *
 * Requires the host activity to extend AppCompatActivity and use an AppCompat theme.
 */
@Singleton
class AppLocaleManager @Inject constructor() {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun apply(language: AppLanguage) {
        val locales = LocaleListCompat.forLanguageTags(language.tag)
        val applyLocales = {
            val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            if (!currentTags.equals(language.tag, ignoreCase = true)) {
                AppCompatDelegate.setApplicationLocales(locales)
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            applyLocales()
        } else {
            mainHandler.post(applyLocales)
        }
    }

    fun current(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales().get(0)?.language
        return if (tag.isNullOrBlank()) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.fromTag(tag)
        }
    }
}
