package com.cgcel.gbiacarddoge.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userData")

        private val USER_TOKEN = stringPreferencesKey("user_token")
        private val USER_SESSIONID = stringPreferencesKey("user_sessionID")
        private val USER_PHYCARDID = stringPreferencesKey("user_phyCardId")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val getUserToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }
    val getUserSessionID: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_SESSIONID] ?: ""
    }
    val getUserPhyCardId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_PHYCARDID] ?: ""
    }
    val getUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    suspend fun saveUserToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun saveUserSessionID(sessionID: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_SESSIONID] = sessionID
        }
    }

    suspend fun saveUserPhyCardId(phyCardId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PHYCARDID] = phyCardId
        }
    }

    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }
}