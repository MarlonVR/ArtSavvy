package com.example.artsavvy.di

import com.google.firebase.database.FirebaseDatabase
import com.example.artsavvy.manager.ArtManager
import com.example.artsavvy.manager.CommentsManager
import com.example.artsavvy.manager.ExhibitionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideArtManager(database: FirebaseDatabase): ArtManager {
        return ArtManager(database)
    }

    @Provides
    @Singleton
    fun provideCommentsManager(database: FirebaseDatabase): CommentsManager {
        return CommentsManager(database)
    }

    @Provides
    @Singleton
    fun provideExhibitionManager(database: FirebaseDatabase): ExhibitionManager {
        return ExhibitionManager(database)
    }

}