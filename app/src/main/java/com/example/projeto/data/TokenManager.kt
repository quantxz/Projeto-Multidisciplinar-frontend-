package com.example.projeto.data

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(
    name = "auth_preferences"
)

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[TOKEN_KEY]
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }


    fun getUserId(): String? {
        val token = runBlocking {
            getToken()
        } ?: return null

        return try {
            val partes = token.split(".")

            if (partes.size < 2) {
                return null
            }

            val payload = String(
                Base64.decode(
                    partes[1],
                    Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                )
            )

            val json = JSONObject(payload)

            json.optString("nameid").takeIf { it.isNotEmpty() }
                ?: json.optString(
                    "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier"
                ).takeIf { it.isNotEmpty() }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}