package com.comet.freetester.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_item")
data class GalleryItemEntity(
    @PrimaryKey() var id: String?,
    var uid: String?,
    var title: String?,
    var note: String?,
    var photoUri: String?,
    var createdAt: Long,
)