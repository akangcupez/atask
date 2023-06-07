package com.akangcupez.atask.di

import com.akangcupez.atask.data.prefs.Sessions
import com.akangcupez.atask.data.repository.CalculationRepository
import com.akangcupez.atask.domain.UseCases
import com.akangcupez.atask.domain.usecase.CalculationListUseCase
import com.akangcupez.atask.domain.usecase.DeleteCalculationUseCase
import com.akangcupez.atask.domain.usecase.InsertCalculationUseCase
import com.akangcupez.atask.domain.usecase.ProcessOcrUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideCalculationUseCases(repository: CalculationRepository, sessions: Sessions): UseCases {
        return UseCases(
            calculationList = CalculationListUseCase(repository, sessions),
            deleteCalculation = DeleteCalculationUseCase(repository, sessions),
            insertCalculation = InsertCalculationUseCase(repository, sessions),
            processOcr = ProcessOcrUseCase()
        )
    }
}
