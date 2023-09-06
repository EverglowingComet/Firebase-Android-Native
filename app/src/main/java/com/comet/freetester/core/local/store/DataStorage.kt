package com.comet.freetester.core.local.store

import kotlinx.coroutines.flow.Flow


interface DataStorage {
    fun getCurrentUidFlow() : Flow<String>
    suspend fun setCurrentUid(uid: String?)
}