package com.business.insurancesmc

import android.app.Application
import androidx.work.Configuration
import com.business.insurancesmc.presentations.notification.InsuranceWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MaruthiConsultancyInsurance : Application() , Configuration.Provider {

    @Inject
    lateinit var workerFactory: InsuranceWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // WorkManager is initialized automatically via Hilt
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }}