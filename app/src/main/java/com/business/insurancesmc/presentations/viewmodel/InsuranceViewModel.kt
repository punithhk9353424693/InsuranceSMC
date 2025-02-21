package com.business.insurancesmc.presentations.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.data.repo.InsurancePerson
import com.business.insurancesmc.presentations.view.GetAllInsurances
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.apache.logging.log4j.message.Message
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val repo: InsurancePerson,
    private val getAllInsurances: GetAllInsurances
) : ViewModel() {

    private val _insurances =
        MutableStateFlow<List<InsuranceCostumer>>(emptyList())
    val insurances: StateFlow<List<InsuranceCostumer>> get() = _insurances


    init {
        // This should be used to fetch the list of inquiries when the ViewModel is initialized
        viewModelScope.launch {
            // Collect data from getInquiriesUseCase and update _inquiries
            getAllInsurances.invoke().collect { insurancessList ->
                _insurances.value = insurancessList
            }
        }
    }

    fun getAllInsurances() {
        viewModelScope.launch {
            getAllInsurances.invoke().collect { inquiriesList ->
                _insurances.value = inquiriesList // Update _inquiries state
            }
        }
    }

    fun setInsurances(insuranceList: List<InsuranceCostumer>) {
        _insurances.value = insuranceList
    }

    suspend fun insertInsurance(insurance: InsuranceCostumer) {
        repo.insertInsurance(insurance)
    }

    fun searchInsurance(query: String) {
        val filterQuery = "%$query%"
        viewModelScope.launch {
            repo.searchInsurance(filterQuery).collect() { insurances ->
                _insurances.value = insurances
            }
        }
    }

    fun deleteInsurance(id: Int) {

        viewModelScope.launch() {
            repo.deleteInsurance(id)
        }
    }

    fun updateInsurance(insurance: InsuranceCostumer) {
        try {

            viewModelScope.launch() {
                repo.updateInsurance(insurance)
               getAllInsurances()

            }
        } catch (e: Exception) {
            Log.d("ViewModelError", "Not Updating: ${e.message}")

        }
    } // In your ViewModel

    fun getSortedInsuranceList(): Flow<List<InsuranceCostumer>> {
        return repo.getAllInsurance()
            .map { insurancesList ->
                insurancesList.sortedWith(compareBy {
                    when (it.status.trim()) {
                        "Open" -> 0
                        "Progress" -> 1
                        "Done" -> 2
                        else -> 3
                    }

                })


            }}
}