package com.comet.freetester.core.home

import androidx.lifecycle.LiveData
import com.comet.freetester.core.remote.callback.AsyncApiCallback
import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.core.remote.data.UserProfile
import kotlinx.coroutines.CoroutineScope

interface HomeRepository {
    fun getCurrentUserFlow() : LiveData<UserProfile?>

    fun loadGalleryData(scope: CoroutineScope, callback: AsyncApiCallback)

    fun saveGalleryUpdate(scope: CoroutineScope, update: GalleryItem, callback: AsyncApiCallback)
}