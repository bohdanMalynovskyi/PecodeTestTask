package com.example.pecodetesttask

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PecodeTestTaskPreferences")

class PecodeTestTaskPreferences(private val context: Context) {

    companion object {
        val FRAGMENT_COUNT = intPreferencesKey(name = "fragment_count")
    }

    suspend fun saveFragmentCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[FRAGMENT_COUNT] = count
        }
    }

    fun getFragmentCount(): Flow<Int> = context.dataStore.data
        .map { preferences ->
            val count = preferences[FRAGMENT_COUNT]
            if (count != null && count > 0) count else 1
        }
}