package com.comet.freetester.core.local

import androidx.lifecycle.LiveData
import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.core.remote.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun insertUserProfile(item: UserProfile)

    suspend fun insertUserProfiles(list: List<UserProfile>)

    suspend fun insertGalleryItem(item: GalleryItem)

    suspend fun insertGalleryItems(list: List<GalleryItem>)

    fun getUserProfile(uid: String?) : UserProfile?

    fun getUserProfileLiveData(uid: String?) : LiveData<UserProfile?>

    fun getGalleryItemById(id: String?) : Flow<GalleryItem>

    fun getGalleryItemsByUid(uid: String?) : Flow<List<GalleryItem>>

}