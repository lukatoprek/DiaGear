package com.example.diagearandroid.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

/**
 * Lightweight, dependency-free locale manager for switching the app language at runtime.
 *
 * The chosen language tag is persisted in [SharedPreferences]. [wrap] is applied in
 * `MainActivity.attachBaseContext` so that every `stringResource` resolves against the
 * forced locale; calling [setLanguage] followed by `Activity.recreate()` switches language.
 */
object LocaleHelper {
    private const val PREFS_NAME = "settings"
    private const val KEY_LANGUAGE = "app_language"

    /** Supported language tags — they match the `values-en-rGB` / `values-hr-rHR` resource folders. */
    const val ENGLISH = "en-GB"
    const val CROATIAN = "hr-HR"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The user's explicitly saved language, or null if they have never picked one. */
    fun getPersistedLanguage(context: Context): String? =
        prefs(context).getString(KEY_LANGUAGE, null)

    /** The language currently in effect: the saved choice, or one derived from the system locale. */
    fun getCurrentLanguage(context: Context): String =
        getPersistedLanguage(context) ?: defaultFromSystem()

    private fun defaultFromSystem(): String =
        if (Locale.getDefault().language == "hr") CROATIAN else ENGLISH

    fun setLanguage(context: Context, tag: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, tag).apply()
    }

    /** Returns a context whose configuration is forced to the effective language. */
    fun wrap(context: Context): Context {
        val locale = Locale.forLanguageTag(getCurrentLanguage(context))
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
