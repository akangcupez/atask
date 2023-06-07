package com.akangcupez.atask.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akangcupez.atask.data.entity.CalculationEntity

@Dao
interface CalculationDao {

    @Query("SELECT * FROM calculation")
    suspend fun getCalculations(): List<CalculationEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(entity: CalculationEntity)

    @Delete
    suspend fun deleteCalculation(entity: CalculationEntity)
}