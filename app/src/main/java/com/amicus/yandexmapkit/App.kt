package com.amicus.yandexmapkit

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.maps.mobile.BuildConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Reading API key from BuildConfig.
        // Do not forget to add your MAPKIT_API_KEY property to local.properties file.
        MapKitFactory.setApiKey("6b608d70-21fd-4950-86a3-60820cc78297")
        MapKitFactory.initialize(this)
    }
}