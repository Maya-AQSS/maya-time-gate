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
    companion object { //informacion guardada en la DataStore
        val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
        val URL_KEY = stringPreferencesKey("url_odoo")
    }

    //funcionaes para guardar y enviar la informacion del DataStore
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

    val formattedUrl: Flow<String> = urlName.map { buildBaseUrl(it) }

    suspend fun setUrl(name: String) {
        context.dataStore.edit { preferences ->
            preferences[URL_KEY] = name
        }
    }

    //Funcion para comprobar si la url de odoo puesta en configuracion es correcta
    private fun buildBaseUrl(rawUrl: String): String {
        var url = rawUrl.trim()

        if (url.isEmpty()) {
            url = "10.42.0.1"
        }

        // Añadir http si falta
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        // Añadir puerto si falta
        if (!url.contains(":8069")) {
            url = "$url:8069"
        }

        // Añadir barra inclinada final
        if (!url.endsWith("/")) {
            url += "/"
        }

        return url
    }
}
