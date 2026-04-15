package com.example.mayatimegate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//Crea la instancia de Data Store en el disco
val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class SettingsManager(private val context: Context) {
    companion object {
        val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
        val URL_KEY = stringPreferencesKey("url_odoo")
    }

    val deviceName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_NAME_KEY] ?: "Terminal 01"
    }

    suspend fun setDeviceName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_NAME_KEY] = name
        }
    }

    val urlName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[URL_KEY] ?: "10.42.0.1"
    }

    suspend fun setUrl(name: String) {
        context.dataStore.edit { preferences ->
            preferences[URL_KEY] = name
        }
    }


}