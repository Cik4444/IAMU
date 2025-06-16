package com.example.boxingapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferenceRepository(private val context: Context) {
    companion object {
        private val LOGGED_IN_USER_KEY = stringPreferencesKey("logged_in_user")
    }

    val loggedInUserFlow: Flow<String?> = context.userDataStore.data
        .map { prefs -> prefs[LOGGED_IN_USER_KEY] }

    suspend fun setLoggedInUser(username: String?) {
        context.userDataStore.edit { prefs ->
            if (username == null) {
                prefs.remove(LOGGED_IN_USER_KEY)
            } else {
                prefs[LOGGED_IN_USER_KEY] = username
            }
        }
    }
}
