package com.comet.freetester.core.home

import com.comet.freetester.core.local.LocalDataSource
import com.comet.freetester.core.remote.RemoteDataSource
import com.comet.freetester.core.remote.callback.AsyncApiCallback
import com.comet.freetester.core.remote.data.GalleryItem
import com.comet.freetester.core.remote.data.UserProfile
import com.comet.freetester.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : HomeRepository {

    override fun loadGalleryData(scope: CoroutineScope, callback: AsyncApiCallback) {
        remoteDataSource.loadGalleryData(object : AsyncApiCallback {
            override fun onSuccess(dict: HashMap<String, Any?>?) {

                val galleryList = ArrayList<GalleryItem>()
                val userProfileList = ArrayList<UserProfile>()
                Utils.getDataMapForKey(dict, "galleryList")?.let { map ->
                    for (id in map.keys) {
                        val item = Utils.getDataMapForKey(map, id)
                        galleryList.add(GalleryItem.fromMap(item))
                    }

                }
                Utils.getDataMapForKey(dict, "userList")?.let { map ->
                    for (id in map.keys) {
                        val item = Utils.getDataMapForKey(map, id)
                        userProfileList.add(UserProfile.fromMap(item))
                    }

                }
                scope.launch {
                    localDataSource.insertGalleryItems(galleryList)
                    localDataSource.insertUserProfiles(userProfileList)
                }
                callback.onSuccess(dict)
            }

            override fun onFailure(msg: String?) {
                callback.onFailure(msg)
            }
        })
    }

    override fun saveGalleryUpdate(scope: CoroutineScope, update: GalleryItem, callback: AsyncApiCallback) {
        remoteDataSource.submitGallery(update, object : AsyncApiCallback {
            override fun onSuccess(dict: HashMap<String, Any?>?) {
                scope.launch {
                    localDataSource.insertGalleryItem(update)
                }
                callback.onSuccess(dict)
            }

            override fun onFailure(msg: String?) {
                callback.onFailure(msg)
            }

        })
    }
}