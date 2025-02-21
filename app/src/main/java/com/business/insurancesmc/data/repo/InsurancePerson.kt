package com.business.insurancesmc.data.repo

import com.business.insurancesmc.data.model.InsuranceCostumer
import kotlinx.coroutines.flow.Flow

interface InsurancePerson {
    suspend fun insertInsurance(insuranceCostumer: InsuranceCostumer)
    suspend fun deleteInsurance(id: Int)
    suspend fun updateInsurance(insuranceCostumer: InsuranceCostumer)
    fun getAllInsurance(): Flow<List<InsuranceCostumer>>
    fun findByInsuranceId(id: Int)
    suspend fun searchInsurance(query: String): Flow<List<InsuranceCostumer>>
    suspend fun addAllInsurances(insurances: List<InsuranceCostumer>)

}