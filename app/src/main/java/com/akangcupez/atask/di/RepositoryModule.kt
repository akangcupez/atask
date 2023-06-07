package com.akangcupez.atask.di

import com.akangcupez.atask.data.repository.CalculationRepository
import com.akangcupez.atask.data.repositoryImpl.CalculationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCalculationRepository(calculationRepositoryImpl: CalculationRepositoryImpl): CalculationRepository
}
