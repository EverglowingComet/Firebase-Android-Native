package com.comet.freetester.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_item")
data class GalleryItemEntity(
    @PrimaryKey val id: String,
    val uid: String?,
    val title: String?,
    val note: String?,
    val photoUri: String?,
    val createdAt: Long,
)