package com.nutrino.audiocutter.di.module

import com.nutrino.audiocutter.data.RepoImpl.RevenueCatRepoImpl
import com.nutrino.audiocutter.domain.Repository.RevenueCatRepository
import com.nutrino.audiocutter.domain.UseCases.revenueCat.GetAllPackagesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RevenueCatModule {
    @Provides
    fun provideRevenueCatRepo(): RevenueCatRepository{
        return RevenueCatRepoImpl()
    }

    @Provides
    fun provideGetPackageUseCase(revenueCatRepository: RevenueCatRepository): GetAllPackagesUseCase{
        return GetAllPackagesUseCase(revenueCatRepository = revenueCatRepository)
    }
}