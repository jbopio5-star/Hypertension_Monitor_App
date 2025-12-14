package com.opio.hypertensionmonitorapp.di

import android.content.Context
import com.opio.hypertensionmonitorapp.data.AppDatabase
import com.opio.hypertensionmonitorapp.repository.AppRepository
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
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRepository(database: AppDatabase): AppRepository {
        return AppRepository(database)
    }
}