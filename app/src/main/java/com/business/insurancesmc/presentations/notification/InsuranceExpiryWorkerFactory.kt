package com.business.insurancesmc.presentations.notification
import InsuranceExpiryWorker
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.business.insurancesmc.data.repo.InsurancePerson
import javax.inject.Inject

class InsuranceWorkerFactory @Inject constructor(
    private val insurancRepo: InsurancePerson // Inject the ViewModel
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParams: WorkerParameters
    ): Worker? {
        return when (workerClassName) {
            InsuranceExpiryWorker::class.java.name -> {
                InsuranceExpiryWorker(appContext, workerParams, insurancRepo)
            }

            else -> null
        } as Worker?
    }
}
