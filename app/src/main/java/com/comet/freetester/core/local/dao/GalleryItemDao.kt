package com.comet.freetester.core.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.comet.freetester.core.local.entity.GalleryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GalleryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GalleryItemEntity>)

    @Query("Select * From gallery_item Where id=:id")
    fun getGalleryItemById(id: String?) : Flow<GalleryItemEntity>

    @Transaction
    @Query("Select * From gallery_item Where uid=:uid")
    fun getGalleryItemsByUid(uid: String?) : Flow<List<GalleryItemEntity>>

}