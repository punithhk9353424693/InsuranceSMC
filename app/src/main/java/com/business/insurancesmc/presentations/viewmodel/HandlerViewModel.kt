package com.business.insurancesmc.presentations.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkManager
import java.util.UUID

class HandlerViewModel(application: Application) : AndroidViewModel(application) {
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    private val _importStatus = MutableLiveData<String>()
    val importStatus: LiveData<String> get() = _importStatus
    fun observeWorkProgress(context: Context, workId: String) {
        try {
            val uuid = UUID.fromString(workId) // ✅ Ensure valid UUID
            val liveData = WorkManager.getInstance(context).getWorkInfoByIdLiveData(uuid)

            liveData.observeForever { workInfo ->
                workInfo?.let {
                    val progress = it.progress.getInt("PROGRESS", 0)
                    _progress.postValue(progress) // ✅ Update Progress Bar

                    if (it.state.isFinished) {
                        _importStatus.postValue("Completed")
                        Log.d("progress", "Handler Progress completed")
                    } else {
                        _importStatus.postValue("Importing...")
                        Log.d("progress", "Handler Progress importing")
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e("HandlerViewModel", "Invalid Work ID: $workId", e)
        }
    }
}