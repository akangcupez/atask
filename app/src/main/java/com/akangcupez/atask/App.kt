package com.akangcupez.atask

import androidx.multidex.MultiDexApplication
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    companion object {
        @get:Synchronized
        lateinit var context: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        //FirebaseApp.initializeApp(this)
        Hawk.init(this).build()
    }
}
