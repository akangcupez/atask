package com.akangcupez.atask.data.prefs

import android.content.SharedPreferences
import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.utils.Const
import com.orhanobut.hawk.Hawk

class Sessions(private val pref: SharedPreferences) : Cache {

    override var storageType: Const.StorageType
        get() {
            val name = pref.getString(Cache.STORAGE_TYPE, Const.StorageType.CACHE.name)
            return if (name == Const.StorageType.DB.name) {
                Const.StorageType.DB
            } else Const.StorageType.CACHE
        }
        set(value) {
            val name = if (value == Const.StorageType.DB) {
                Const.StorageType.DB.name
            } else Const.StorageType.CACHE.name
            pref.edit().putString(Cache.STORAGE_TYPE, name).apply()
        }

    override var calculationResults: List<Calculation>
        get() {
            return try {
                Hawk.get(Cache.CALC_RESULTS, emptyList())
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            try {
                Hawk.put(Cache.CALC_RESULTS, value)
            } catch (e: Exception) {
                //do nothing
            }
        }
}
