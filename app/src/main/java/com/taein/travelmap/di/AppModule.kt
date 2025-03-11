package com.taein.travelmap.di

import android.content.Context
import androidx.room.Room
import com.taein.travelmap.database.AppDatabase
import com.taein.travelmap.database.DiaryDao
import com.taein.travelmap.database.PhotoMarkerDao
import com.taein.travelmap.repository.diary.DiaryRepository
import com.taein.travelmap.repository.diary.OfflineFirstDiaryRepository
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
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "photo_markers.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePhotoMarkerDao(database: AppDatabase): PhotoMarkerDao {
        return database.photoMarkerDao()
    }

    @Provides
    fun providePhotoMarkerRepository(photoMarkerDao: PhotoMarkerDao): PhotoMarkerRepository {
        return OfflineFirstPhotoMarkerRepository(photoMarkerDao)
    }

    @Provides
    fun provideDiaryDao(database: AppDatabase): DiaryDao {
        return database.diaryDao()
    }

    @Provides
    fun provideOfflineFirstDiaryRepository(diaryDao: DiaryDao): DiaryRepository {
        return OfflineFirstDiaryRepository(diaryDao)
    }
}