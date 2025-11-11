package me.onvo.onvo.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import android.content.Context

private lateinit var applicationContext: Context

fun initPreferencesDataStore(appContext: Context) {
    applicationContext = appContext
}


fun getPreferencesDataStorePath(appContext: Context): String =
    appContext.filesDir.resolve(dataStoreFileName).absolutePath

actual fun createPreferencesDataStore(): DataStore<Preferences> {
    val path = getPreferencesDataStorePath(applicationContext)
    return getPreferencesDataStore(path)
}