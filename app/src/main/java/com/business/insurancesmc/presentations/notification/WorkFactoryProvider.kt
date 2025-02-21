package com.business.insurancesmc.presentations.notification

import androidx.work.WorkerFactory
import androidx.work.ListenableWorker
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

// Create the EntryPoint to access dependencies like the WorkerFactory
@EntryPoint
@InstallIn(SingletonComponent::class) // Replace with appropriate component
interface WorkFactoryProvider {
    fun workerFactory(): WorkerFactory
}
