package com.business.insurancesmc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.business.insurancesmc.data.InsuranceDao
import com.business.insurancesmc.data.model.InsuranceCostumer

@Database(entities = [InsuranceCostumer::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {


    abstract fun insuranceDao(): InsuranceDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dream_homes_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}

