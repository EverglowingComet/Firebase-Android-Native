package com.comet.freetester.core.local.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val MAIN_PREF = "main_pref"
const val CURRENT_UID = "current_uid"

class DataStorageImpl @Inject constructor(
    private val context: Context
) : DataStorage {

    private val Context.datastore : DataStore<Preferences> by preferencesDataStore(MAIN_PREF)
    private val curUserKey = stringPreferencesKey(CURRENT_UID)

    override fun getCurrentUidFlow(): Flow<String> {
        return context.datastore.data.map { preferences ->
            preferences[curUserKey]?: ""
        }
    }

    override suspend fun setCurrentUid(uid: String?) {
        context.datastore.edit { settings ->
            settings[curUserKey] = uid ?: ""
        }
    }
}