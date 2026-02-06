package com.store.riderfit

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RiderFitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase se inicializa automáticamente
        Firebase.initialize(this)
    }
}
