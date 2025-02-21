package com.business.insurancesmc.di

import android.content.Context
import com.business.insurancesmc.data.InsuranceDao
import com.business.insurancesmc.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return AppDatabase.getInstance(context)

        }

        @Provides
        fun provideInsuranceDao(db: AppDatabase): InsuranceDao = db.insuranceDao()
    }
