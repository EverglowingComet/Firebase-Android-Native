package com.comet.freetester.core.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.comet.freetester.core.config.AppConfig
import com.comet.freetester.core.local.dao.GalleryItemDao
import com.comet.freetester.core.local.dao.UserProfileDao
import com.comet.freetester.core.local.entity.GalleryItemEntity
import com.comet.freetester.core.local.entity.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        GalleryItemEntity::class,
   ],
    version = 1,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun userProfileDao() : UserProfileDao
    abstract fun galleryItemDao() : GalleryItemDao

    private fun doInitialize() {

    }

    private class MainDatabaseCallback (
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    database.doInitialize()
                }
            }
        }
    }

    companion object {
        @Volatile
        var INSTANCE: MainDatabase? = null

        fun buildDatabase(context: Context, scope: CoroutineScope) : MainDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    AppConfig.DATABASE_NAME
                )
                    .addCallback(MainDatabaseCallback(scope))
                    .addMigrations(*MIGRATIONS)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}