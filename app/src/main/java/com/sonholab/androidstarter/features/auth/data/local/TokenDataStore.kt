package com.sonholab.androidstarter.features.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_prefs",
)

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    /**
     * Returns a [Flow] that emits the stored access token, or `null` if none is saved.
     */
    fun getToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCESS_TOKEN]
    }

    /**
     * Returns a [Flow] that emits the stored refresh token, or `null` if none is saved.
     */
    fun getRefreshToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.REFRESH_TOKEN]
    }

    /**
     * Persists the [accessToken] and optionally a [refreshToken].
     */
    suspend fun saveToken(accessToken: String, refreshToken: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            if (refreshToken != null) {
                prefs[Keys.REFRESH_TOKEN] = refreshToken
            }
        }
    }

    /**
     * Removes all auth tokens from storage.
     */
    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
        }
    }

    /**
     * Returns `true` if an access token is currently stored.
     */
    fun isLoggedIn(): Flow<Boolean> = getToken().map { it != null }
}
