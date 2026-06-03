package com.example.brainbuilder.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brainbuilder_prefs")

class DataStore(private val context: Context) {

    private val JWT_KEY = stringPreferencesKey("jwt_token")

    suspend fun saveToken(jwt: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_KEY] = jwt
        }
    }

    suspend fun getToken(): String {
        return context.dataStore.data.map { prefs ->
            prefs[JWT_KEY] ?: ""
        }.first()
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(JWT_KEY)
        }
    }
}