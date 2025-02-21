package com.business.insurancesmc.presentations.view

import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.data.repo.InsurancePerson
import com.business.insurancesmc.data.repo.InsurancePersonImpl
import kotlinx.coroutines.flow.Flow

class GetAllInsurances ( private val repo: InsurancePerson
) {

    fun invoke(): Flow<List<InsuranceCostumer>> = repo.getAllInsurance()

}