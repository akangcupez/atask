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

class CalculationListUseCase @Inject constructor(
    private val repository: CalculationRepository,
    private val sessions: Sessions
) {

    operator fun invoke(): Flow<Resource<List<Calculation>>> = flow {
        try {
            emit(Resource.Loading())
            val result = if (sessions.storageType == Const.StorageType.DB) {
                val calculations = repository.getCalculations()
                if (calculations.isNotEmpty()) {
                    calculations.map { it.toCalculation() }
                } else emptyList()
            } else sessions.calculationResults

            if (result.isNotEmpty()) {
                emit(Resource.Completed(result))
            } else emit(Resource.Empty())

        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
