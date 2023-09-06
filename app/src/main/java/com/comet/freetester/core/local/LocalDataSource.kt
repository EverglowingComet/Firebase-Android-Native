package com.comet.freetester.core.local

import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.core.remote.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun insertUserProfile(item: UserProfile)

    suspend fun insertUserProfiles(list: List<UserProfile>)

    suspend fun insertGalleryItem(item: GalleryItem)

    suspend fun insertGalleryItems(list: List<GalleryItem>)

    fun getUserProfile(uid: String) : UserProfile?

    fun getUserProfileFlow(uid: String) : Flow<UserProfile>

    fun getGalleryItemById(id: String?) : Flow<GalleryItem>

    fun getGalleryItemsByUid(uid: String?) : Flow<List<GalleryItem>>

}