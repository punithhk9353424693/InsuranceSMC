package com.business.insurancesmc.di
import androidx.work.WorkerFactory
import com.business.insurancesmc.data.repo.InsurancePerson
import com.business.insurancesmc.presentations.notification.InsuranceWorkerFactory
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {


    @Provides
    @Singleton
    fun provideWorkerFactory(
        insurancePerson: InsurancePerson // Injecting repository
    ): WorkerFactory {
        return InsuranceWorkerFactory(insurancePerson)
    }

}