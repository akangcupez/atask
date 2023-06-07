package com.akangcupez.atask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akangcupez.atask.data.dao.CalculationDao
import com.akangcupez.atask.data.entity.CalculationEntity

@Database(entities = [CalculationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val calculationDao: CalculationDao
}
