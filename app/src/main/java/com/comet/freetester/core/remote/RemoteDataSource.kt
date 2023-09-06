package com.comet.freetester.core.remote

import com.comet.freetester.core.remote.callback.AsyncApiCallback
import com.comet.freetester.core.remote.data.GalleryItem

interface RemoteDataSource {

    fun loadGalleryData(callback: AsyncApiCallback)

    fun submitGallery(item: GalleryItem, callback: AsyncApiCallback)

}