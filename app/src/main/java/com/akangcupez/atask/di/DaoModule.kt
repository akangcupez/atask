package com.akangcupez.atask.di

import com.akangcupez.atask.data.AppDatabase
import com.akangcupez.atask.data.dao.CalculationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    @Singleton
    fun provideCalculationDao(db: AppDatabase): CalculationDao {
        return db.calculationDao
    }
}