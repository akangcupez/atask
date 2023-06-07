package com.akangcupez.atask.data.repository

import com.akangcupez.atask.data.entity.CalculationEntity

interface CalculationRepository {

    suspend fun getCalculations(): List<CalculationEntity>

    suspend fun insertCalculation(entity: CalculationEntity)

    suspend fun deleteCalculation(entity: CalculationEntity)
}
