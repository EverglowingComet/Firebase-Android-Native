package com.comet.freetester.core.local.mapper

import com.comet.freetester.core.local.entity.GalleryItemEntity
import com.comet.freetester.core.remote.data.GalleryItem

class GalleryItemToEntityMapper: BaseMapper<GalleryItem, GalleryItemEntity> {
    override fun map(value: GalleryItem): GalleryItemEntity {
        return GalleryItemEntity(
            id = value.id,
            uid = value.uid,
            title = value.title,
            note = value.note,
            photoUri = value.photoUri,
            createdAt = value.createdAt,
        )
    }
}

class EntityToGalleryItemMapper: BaseMapper<GalleryItemEntity, GalleryItem> {
    override fun map(value: GalleryItemEntity): GalleryItem {
        val result = GalleryItem()
        result.id = value.id
        result.uid = value.uid
        result.title = value.title
        result.note = value.note
        result.photoUri = value.photoUri
        result.createdAt = value.createdAt

        return result
    }
}