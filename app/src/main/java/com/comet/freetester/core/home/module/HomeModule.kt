package com.comet.freetester.core.home.module

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.comet.freetester.core.remote.api.MainApis
import com.comet.freetester.core.home.HomeRepository
import com.comet.freetester.core.home.HomeRepositoryImpl
import com.comet.freetester.core.home.di.ApplicationScope
import com.comet.freetester.core.local.LocalDataSource
import com.comet.freetester.core.local.LocalDataSourceImpl
import com.comet.freetester.core.local.dao.GalleryItemDao
import com.comet.freetester.core.local.dao.UserProfileDao
import com.comet.freetester.core.local.db.MainDatabase
import com.comet.freetester.core.local.mapper.EntityToGalleryItemMapper
import com.comet.freetester.core.local.mapper.EntityToProfileMapper
import com.comet.freetester.core.local.mapper.GalleryItemToEntityMapper
import com.comet.freetester.core.local.mapper.ProfileToEntityMapper
import com.comet.freetester.core.local.store.DataStorage
import com.comet.freetester.core.local.store.DataStorageImpl
import com.comet.freetester.core.local.store.MAIN_PREF
import com.comet.freetester.core.remote.RemoteDataSource
import com.comet.freetester.core.remote.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    @Singleton
    fun provideUserProfileToEntityMapper() : ProfileToEntityMapper = ProfileToEntityMapper()
    
    @Provides
    @Singleton
    fun provideGalleryItemToEntityMapper() : GalleryItemToEntityMapper = GalleryItemToEntityMapper()
    
    @Provides
    @Singleton
    fun provideEntityToUserProfileMapper() : EntityToProfileMapper = EntityToProfileMapper()
    
    @Provides
    @Singleton
    fun provideEntityToGalleryItemMapper() : EntityToGalleryItemMapper = EntityToGalleryItemMapper()

    @Provides
    @Singleton
    fun provideMainDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope
    ) : MainDatabase = MainDatabase.buildDatabase(context, scope)
    
    @Provides
    @Singleton
    fun provideUserProfileDao(mainDatabase: MainDatabase) : UserProfileDao = mainDatabase.userProfileDao()
    
    @Provides
    @Singleton
    fun provideGalleryItemDao(mainDatabase: MainDatabase) : GalleryItemDao = mainDatabase.galleryItemDao()

    @Provides
    @Singleton
    fun provideLocalDataSource(
        userProfileDao: UserProfileDao,
        galleryItemDao: GalleryItemDao,
        entityToGalleryItemMapper: EntityToGalleryItemMapper,
        entityToProfileMapper: EntityToProfileMapper,
        profileToEntityMapper: ProfileToEntityMapper,
        galleryItemToEntityMapper: GalleryItemToEntityMapper,
    ) : LocalDataSource = 
        LocalDataSourceImpl(
            userProfileDao,
            galleryItemDao,
            entityToGalleryItemMapper,
            entityToProfileMapper,
            profileToEntityMapper,
            galleryItemToEntityMapper
        )

    @Provides
    @Singleton
    fun provideMainApi() : MainApis = MainApis()

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        api: MainApis
    ) : RemoteDataSource =
        RemoteDataSourceImpl(
            api = api
        )

    @Provides
    @Singleton
    fun provideDataStorage(
        @ApplicationContext context: Context
    ) : DataStorage =
        DataStorageImpl(
            context
        )

    @Provides
    @Singleton
    fun provideHomeRepository(
        dataStorage: DataStorage,
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ) : HomeRepository = HomeRepositoryImpl(
        dataStorage,
        localDataSource,
        remoteDataSource
    )
}