package com.business.insurancesmc.data.repo

import com.business.insurancesmc.data.InsuranceDao
import com.business.insurancesmc.data.model.InsuranceCostumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsurancePersonImpl  @Inject constructor(
    private val dao: InsuranceDao
) : InsurancePerson {


    override suspend fun insertInsurance(insurance: InsuranceCostumer) {
        dao.insertInsurance(insurance)
    }

    override suspend fun deleteInsurance(id: Int) {
        dao.deleteInsurance(id)
    }

    override suspend fun updateInsurance(insurance: InsuranceCostumer) {
        dao.updateInsurance(insurance)
    }

    override fun getAllInsurance(): Flow<List<InsuranceCostumer>> {
        return dao.getAllInsurance()
    }


    override fun findByInsuranceId(id: Int) {
    }

    override suspend fun searchInsurance(query: String): Flow<List<InsuranceCostumer>> {
        return dao.searchInsurance(query)
    }

    override suspend fun addAllInsurances(insurances: List<InsuranceCostumer>) {
        withContext(Dispatchers.IO) {
            dao.addAllInsurance(insurances)
        }
    }

    suspend fun importUsers(userList: List<InsuranceCostumer>) {
        withContext(Dispatchers.IO) {
            dao.addAllInsurance(userList)
        }
    }
}
