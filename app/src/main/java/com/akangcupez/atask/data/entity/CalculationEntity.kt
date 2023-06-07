package com.akangcupez.atask.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akangcupez.atask.domain.model.Calculation

@Entity(tableName = "calculation")
data class CalculationEntity(
    @PrimaryKey var id: Long,
    var input: String,
    var result: String
) {
    fun toCalculation(): Calculation {
        return Calculation(id = id, input = input, result = result)
    }
}
