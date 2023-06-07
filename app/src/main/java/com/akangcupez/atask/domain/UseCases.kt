package com.akangcupez.atask.domain

import com.akangcupez.atask.domain.usecase.CalculationListUseCase
import com.akangcupez.atask.domain.usecase.DeleteCalculationUseCase
import com.akangcupez.atask.domain.usecase.InsertCalculationUseCase
import com.akangcupez.atask.domain.usecase.ProcessOcrUseCase

data class UseCases(
    val calculationList: CalculationListUseCase,
    val deleteCalculation: DeleteCalculationUseCase,
    val insertCalculation: InsertCalculationUseCase,
    val processOcr: ProcessOcrUseCase
)
