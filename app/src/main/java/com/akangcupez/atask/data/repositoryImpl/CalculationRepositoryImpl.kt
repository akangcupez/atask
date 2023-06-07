package com.akangcupez.atask.data.repositoryImpl

import com.akangcupez.atask.data.dao.CalculationDao
import com.akangcupez.atask.data.entity.CalculationEntity
import com.akangcupez.atask.data.repository.CalculationRepository
import javax.inject.Inject

class CalculationRepositoryImpl @Inject constructor(
    private val dao: CalculationDao
) : CalculationRepository {

    override suspend fun getCalculations(): List<CalculationEntity> {
        return dao.getCalculations() ?: emptyList()
    }

    override suspend fun insertCalculation(
        entity: CalculationEntity
    ) {
        dao.insertCalculation(entity)
    }

    override suspend fun deleteCalculation(
        entity: CalculationEntity
    ) {
        dao.deleteCalculation(entity)
    }
}
