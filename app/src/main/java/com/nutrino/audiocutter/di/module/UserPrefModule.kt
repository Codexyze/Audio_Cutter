package com.nutrino.audiocutter.di.module

import android.content.Context
import com.nutrino.audiocutter.data.RepoImpl.UserPrefRepositoryImpl
import com.nutrino.audiocutter.data.userPref.UserPrefrenceStore
import com.nutrino.audiocutter.domain.Repository.UserPrefRepository
import com.nutrino.audiocutter.domain.UseCases.userPref.UserPrefUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object UserPrefModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context{
        return context
    }
    @Provides
    fun provideUserPrefStore(@ApplicationContext context: Context): UserPrefrenceStore{
        return UserPrefrenceStore(context= context)
    }

    @Provides
    fun provideUserPrefRepository(userPrefrenceStore: UserPrefrenceStore): UserPrefRepository {
        return UserPrefRepositoryImpl(userPrefrenceStore = userPrefrenceStore)
    }

    @Provides
    fun provideUserPrefUseCase(userPrefRepository: UserPrefRepository): UserPrefUseCase {
        return UserPrefUseCase(repository = userPrefRepository)
    }
}