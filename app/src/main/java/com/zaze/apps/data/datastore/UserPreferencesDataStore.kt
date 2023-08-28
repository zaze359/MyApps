package com.zaze.apps.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesDataStore private constructor(preferences: DataStore<Preferences>) :
    DataStore<Preferences> by preferences {
    companion object {
        // 委托方式获取dataStore
        private val Context.userDataStore by preferencesDataStore(
            name = "user_preferences.ini"
        )

        fun create(context: Context): UserPreferencesDataStore {
            return UserPreferencesDataStore(context.userDataStore)
        }
    }

    internal object Key {
        // string 类型
        val username = stringPreferencesKey("username")

        // light、dark、auto
        val appThemeMode = stringPreferencesKey("appThemeMode")
    }

    suspend fun setUsername(username: String) {
        edit {
            it[Key.username] = username
        }
    }

    suspend fun removeUsername() {
        edit {
            it.remove(Key.username)
        }
    }

    suspend fun setAppThemeMode(username: String) {
        edit {
            it[Key.appThemeMode] = username
        }
    }


    class _Scope(private val preferences: Preferences) : UserPreferencesScope {
        override val username: String?
            get() = preferences[Key.username]
        override val appThemeMode: String?
            get() = preferences[Key.appThemeMode]
    }

    interface UserPreferencesScope {
        val username: String?
        val appThemeMode: String?

    }
}

suspend fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
    return data.first()[key]
}
fun <T> UserPreferencesDataStore.map(block: UserPreferencesDataStore.UserPreferencesScope.(Preferences) -> T): Flow<T> {
    return data
        .catch { exception ->
            // 处理异常
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            block(UserPreferencesDataStore._Scope(preferences), preferences)
        }
}
