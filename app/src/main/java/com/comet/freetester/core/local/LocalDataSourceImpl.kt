package com.comet.freetester.core.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.comet.freetester.core.local.dao.GalleryItemDao
import com.comet.freetester.core.local.dao.UserProfileDao
import com.comet.freetester.core.local.mapper.EntityToGalleryItemMapper
import com.comet.freetester.core.local.mapper.EntityToProfileMapper
import com.comet.freetester.core.local.mapper.GalleryItemToEntityMapper
import com.comet.freetester.core.local.mapper.ProfileToEntityMapper
import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.core.remote.data.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val galleryItemDao: GalleryItemDao,
    private val entityToGalleryItemMapper: EntityToGalleryItemMapper,
    private val entityToProfileMapper: EntityToProfileMapper,
    private val profileToEntityMapper: ProfileToEntityMapper,
    private val galleryItemToEntityMapper: GalleryItemToEntityMapper,
) : LocalDataSource {
    override suspend fun insertUserProfile(item: UserProfile) {
        userProfileDao.insert(profileToEntityMapper.map(item))
    }

    override suspend fun insertUserProfiles(list: List<UserProfile>) {
        val items = list.map { profileToEntityMapper.map(it) }
        userProfileDao.insertAll(items)
    }

    override suspend fun insertGalleryItem(item: GalleryItem) {
        galleryItemDao.insert(galleryItemToEntityMapper.map(item))
    }

    override suspend fun insertGalleryItems(list: List<GalleryItem>) {
        val items = list.map { galleryItemToEntityMapper.map(it) }
        galleryItemDao.insertAll(items)
    }

    override fun getUserProfile(uid: String?): UserProfile? {
        return userProfileDao.getUserProfileByUid(uid)?.let { entityToProfileMapper.map(it) }
    }

    override fun getUserProfileLiveData(uid: String?): LiveData<UserProfile?> {
        return userProfileDao.getUserProfileFlowByUid(uid).map { entity ->
            entity?.let {
                entityToProfileMapper.map(entity)
            }
        }
    }

    override fun getGalleryItemById(id: String?): Flow<GalleryItem> {
        return galleryItemDao.getGalleryItemById(id).map { entityToGalleryItemMapper.map(it) }
    }

    override fun getGalleryItemsByUid(uid: String?): Flow<List<GalleryItem>> {
        return galleryItemDao.getGalleryItemsByUid(uid).map { list ->
                list.map { entityToGalleryItemMapper.map(it) }
        }
    }
}