package com.business.insurancesmc.di

import android.content.Context
import androidx.work.WorkerFactory
import com.business.insurancesmc.data.InsuranceDao
import com.business.insurancesmc.data.repo.InsurancePerson
import com.business.insurancesmc.data.repo.InsurancePersonImpl
import com.business.insurancesmc.presentations.view.GetAllInsurances
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideInsuranceRepository(dao: InsuranceDao): InsurancePerson {
        return InsurancePersonImpl(dao)
    }

    @Provides
    @Singleton
    fun provideGetAllInsuranceInstance(repo: InsurancePerson): GetAllInsurances{
        return GetAllInsurances(repo)
    }




}