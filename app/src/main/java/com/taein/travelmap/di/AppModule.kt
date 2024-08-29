package com.taein.travelmap.di

import android.content.Context
import androidx.room.Room
import com.taein.travelmap.database.AppDatabase
import com.taein.travelmap.database.PhotoMarkerDao
import com.taein.travelmap.repository.photoMarker.OfflineFirstPhotoMarkerRepository
import com.taein.travelmap.repository.photoMarker.PhotoMarkerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "photo_markers.db").build()
    }

    @Provides
    fun providePhotoMarkerDao(database: AppDatabase): PhotoMarkerDao {
        return database.photoMarkerDao()
    }

    @Provides
    fun providePhotoMarkerRepository(photoMarkerDao: PhotoMarkerDao): PhotoMarkerRepository {
        return OfflineFirstPhotoMarkerRepository(photoMarkerDao)
    }
}