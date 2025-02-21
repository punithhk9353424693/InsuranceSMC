package com.business.insurancesmc.presentations.insurancepractical

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.business.insurancesmc.data.local.AppDatabase
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ExportWorker (context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("ExportWorker", "Worker Started") //  Worker execution log

        val filePath = inputData.getString("FILE_PATH") ?: return Result.failure()
        Log.d("ExportWorker", "File Path: $filePath") //  Log file path

        return try {
            val file = File(filePath)
            if (!file.exists()) {
                Log.e("ExportWorker", "File does not exist") //  Log file issue
                return Result.failure()
            }

            val json = file.readText()
            val insuranceUsers: List<InsuranceCostumer> = Gson().fromJson(
                json, object : TypeToken<List<InsuranceCostumer>>() {}.type
            )

            val database = AppDatabase.getInstance(applicationContext)
            val dao = database.insuranceDao()

            withContext(Dispatchers.IO) {
                val totalBatches = (insuranceUsers.size / 100) + 1
                Log.d("ExportWorker", "📊 Total Batches: $totalBatches")
                insuranceUsers.chunked(100).forEachIndexed { index, batch ->
                    database.runInTransaction {
                        runBlocking {
                            dao.addAllInsurance(batch)
                        }
                    }

                    val progress = ((index + 1) * 100) / totalBatches
                    Log.d("ExportWorker", "Progress: $progress") // ✅ Log progress
                    setProgress(workDataOf("PROGRESS" to progress))
                }
            }

            setProgress(workDataOf("PROGRESS" to 100))
            Log.d("ExportWorker", "Worker Completed ") //  Log success
            return Result.success()
        } catch (e: Exception) {
            Log.e("ExportWorker", "Worker Failed", e) //  Log failure
            return Result.failure()
        }
    }
}