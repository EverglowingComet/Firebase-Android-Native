package com.comet.freetester.core.remote

import com.comet.freetester.core.remote.api.MainApis
import com.comet.freetester.core.remote.callback.AsyncApiCallback
import com.comet.freetester.core.remote.data.GalleryItem
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val api: MainApis,
) : RemoteDataSource {
    override fun loadGalleryData(callback: AsyncApiCallback) {
        val data: HashMap<String, Any> = HashMap()
        api.invokeApiCall("delivery-galleryQuery", data, callback)
    }

    override fun submitGallery(item: GalleryItem, callback: AsyncApiCallback) {
        TODO("Not yet implemented")
    }
}