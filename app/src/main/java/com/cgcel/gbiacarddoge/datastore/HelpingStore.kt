package com.cgcel.gbiacarddoge.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class SingleHelpingData(
    val user_token: String,
    val user_sessionId: String,
    val user_phyCardId: String,
    val user_username: String,
    val user_userId: String,
    val createTime: String
)

class HelpingStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("helpingData")

        private val HELPING_DATA = stringPreferencesKey("helping_data")
    }

    val getHelpingData: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[HELPING_DATA] ?: ""
    }

    suspend fun saveHelpingData(helpingData: String) {
        context.dataStore.edit { preferences ->
            preferences[HELPING_DATA] = helpingData
        }
    }
}