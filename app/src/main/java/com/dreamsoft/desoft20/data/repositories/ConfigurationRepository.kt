package com.dreamsoft.desoft20.data.repositories

import com.dreamsoft.desoft20.data.models.AppConfiguration
import com.dreamsoft.desoft20.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    suspend fun getConfiguration(): AppConfiguration {
        return preferencesManager.getConfiguration()
    }

    suspend fun saveConfiguration(configuration: AppConfiguration) {
        preferencesManager.saveConfiguration(configuration)
    }

    fun getConfigurationFlow(): Flow<AppConfiguration> {
        return preferencesManager.configurationFlow
    }
}