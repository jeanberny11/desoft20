package com.dreamsoft.desoft20.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.dreamsoft.desoft20.data.models.AppConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val LOCAL_URL_KEY = stringPreferencesKey("local_url")
        private val REMOTE_URL_KEY = stringPreferencesKey("remote_url")
        private val PRINTER_NAME_KEY = stringPreferencesKey("printer_name")
        private val USE_LOCAL_URL_KEY = booleanPreferencesKey("use_local_url")
        private val ENABLE_CACHE_KEY = booleanPreferencesKey("enable_cache")
        private val ENABLE_ZOOM_KEY = booleanPreferencesKey("enable_zoom")
        private val ENABLE_WEB_NAVIGATION_KEY = booleanPreferencesKey("enable_web_navigation")
    }

    val configurationFlow: Flow<AppConfiguration> = dataStore.data.map { preferences ->
        AppConfiguration(
            localUrl = preferences[LOCAL_URL_KEY] ?: "",
            remoteUrl = preferences[REMOTE_URL_KEY] ?: "",
            printerName = preferences[PRINTER_NAME_KEY] ?: "",
            useLocalUrl = preferences[USE_LOCAL_URL_KEY] ?: false,
            enableCache = preferences[ENABLE_CACHE_KEY] ?: false,
            enableZoom = preferences[ENABLE_ZOOM_KEY] ?: false,
            enableWebNavigation = preferences[ENABLE_WEB_NAVIGATION_KEY] ?: false
        )
    }

    suspend fun getConfiguration(): AppConfiguration {
        return configurationFlow.first()
    }

    suspend fun saveConfiguration(configuration: AppConfiguration) {
        dataStore.edit { preferences ->
            preferences[LOCAL_URL_KEY] = configuration.localUrl
            preferences[REMOTE_URL_KEY] = configuration.remoteUrl
            preferences[PRINTER_NAME_KEY] = configuration.printerName
            preferences[USE_LOCAL_URL_KEY] = configuration.useLocalUrl
            preferences[ENABLE_CACHE_KEY] = configuration.enableCache
            preferences[ENABLE_ZOOM_KEY] = configuration.enableZoom
            preferences[ENABLE_WEB_NAVIGATION_KEY] = configuration.enableWebNavigation
        }
    }
}