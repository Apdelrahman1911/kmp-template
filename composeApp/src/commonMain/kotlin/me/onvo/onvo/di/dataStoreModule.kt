package me.onvo.onvo.di


// di module
import me.onvo.onvo.core.datastore.PreferencesManager
import org.koin.dsl.module
import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.Preferences
import me.onvo.onvo.core.datastore.createPreferencesDataStore

val dataStoreModule = module {
    // make DataStore<Preferences> available
    single<DataStore<Preferences>> { createPreferencesDataStore() }

    // PreferencesManager uses the DataStore
    single { PreferencesManager(get()) }
}
