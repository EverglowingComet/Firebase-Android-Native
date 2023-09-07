package com.comet.freetester.core.local.store

import androidx.lifecycle.LiveData


interface DataStorage {
    fun getCurrentUidFlow() : LiveData<String?>
    suspend fun setCurrentUid(uid: String?)
}