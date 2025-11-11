package me.onvo.onvo.core.datastore

// File: composeApp/src/jvmMain/kotlin/me/onvo/onvo/core/datastore/DataStoreFactory.jvm.kt

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File
import okio.Path
import okio.Path.Companion.toPath


    actual fun createPreferencesDataStore(): DataStore<Preferences> {
        return androidx.datastore.preferences.core.PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val userHome = System.getProperty("user.home")
                val appDir = File(userHome, ".onvo")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }
                // Return an okio.Path
                (appDir.absolutePath.toPath() / dataStoreFileName)
            }
        )
    }


