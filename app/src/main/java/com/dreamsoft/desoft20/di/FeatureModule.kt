package com.dreamsoft.desoft20.di

import android.content.Context
import com.dreamsoft.desoft20.features.bluetooth.BluetoothManager
import com.dreamsoft.desoft20.features.download.DownloadManager
import com.dreamsoft.desoft20.features.location.LocationManager
import com.dreamsoft.desoft20.features.printer.GenericPrinterManager
import com.dreamsoft.desoft20.features.share.ShareManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeatureModule {

    @Provides
    @Singleton
    fun providePrinterManager(
        @ApplicationContext context: Context
    ): GenericPrinterManager = GenericPrinterManager(context)

    @Provides
    @Singleton
    fun provideLocationManager(
    ): LocationManager = LocationManager()

    @Provides
    @Singleton
    fun provideBluetoothManager(
    ): BluetoothManager = BluetoothManager()

    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context
    ): DownloadManager = DownloadManager(context)

    @Provides
    @Singleton
    fun provideShareManager(
        @ApplicationContext context: Context
    ): ShareManager = ShareManager(context)
}