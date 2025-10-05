package com.dreamsoft.desoft20.di

import android.content.Context
import com.dreamsoft.desoft20.data.preferences.PreferencesManager
import com.dreamsoft.desoft20.data.repositories.ConfigurationRepository
import com.dreamsoft.desoft20.data.repositories.PreferencesRepository
import com.dreamsoft.desoft20.utils.network.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideNetworkUtils(
        @ApplicationContext context: Context
    ): NetworkUtils = NetworkUtils(context)

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesManager: PreferencesManager
    ): PreferencesRepository = PreferencesRepository(preferencesManager)

    @Provides
    @Singleton
    fun provideConfigurationRepository(
        preferencesManager: PreferencesManager
    ): ConfigurationRepository = ConfigurationRepository(preferencesManager)
}