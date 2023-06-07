package com.akangcupez.atask.domain.usecase

import com.akangcupez.atask.data.prefs.Sessions
import com.akangcupez.atask.data.repository.CalculationRepository
import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.utils.Const
import com.akangcupez.atask.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class DeleteCalculationUseCase @Inject constructor(
    private val repository: CalculationRepository,
    private val sessions: Sessions
) {

    operator fun invoke(calculation: Calculation): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            if (sessions.storageType == Const.StorageType.DB) {
                repository.deleteCalculation(calculation.toCalculationEntity())
            } else {
                val results = sessions.calculationResults
                if (results.isNotEmpty()) {
                    results.filter { it.id != calculation.id }.let {
                        sessions.calculationResults = it
                    }
                }
            }
            emit(Resource.Completed(Unit))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}