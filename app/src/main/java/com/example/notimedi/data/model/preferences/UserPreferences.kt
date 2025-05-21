package com.example.notimedi.data.model.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("notimedi_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val CURRENT_USER = stringPreferencesKey("current_user")
        val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
    }

    suspend fun setLoginStatus(isLoggedIn: Boolean, username: String = "") {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = isLoggedIn
            prefs[CURRENT_USER] = username
        }
    }

    fun getLoginStatus(): Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }

    fun getCurrentUser(): Flow<String?> = context.dataStore.data.map {
        it[CURRENT_USER]
    }

    suspend fun setOnboardingShown() {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_SHOWN] = true
        }
    }

    fun isOnboardingShown(): Flow<Boolean> = context.dataStore.data.map {
        it[ONBOARDING_SHOWN] ?: false
    }

    suspend fun clearLoginStatus() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs[CURRENT_USER] = ""
        }
    }
}