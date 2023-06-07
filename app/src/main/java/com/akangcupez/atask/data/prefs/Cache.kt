package com.akangcupez.atask.data.prefs

import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.utils.Const

interface Cache {

    companion object {
        const val STORAGE_TYPE = "storage_type"
        const val CALC_RESULTS = "calc_results"
    }

    var storageType: Const.StorageType

    var calculationResults: List<Calculation>
}