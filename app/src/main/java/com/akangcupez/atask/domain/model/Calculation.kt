package com.akangcupez.atask.domain.model

import android.os.Parcelable
import com.akangcupez.atask.data.entity.CalculationEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Calculation(
    var id: Long,
    var input: String,
    var result: String
) : Parcelable {

    fun toCalculationEntity(): CalculationEntity {
        return CalculationEntity(id = id, input = input, result = result)
    }
}
