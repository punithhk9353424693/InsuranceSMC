package com.business.insurancesmc.presentations.view

import android.util.Log
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun filterByCurrentByMonth(
    insuranceList: List<InsuranceCostumer>,
): List<InsuranceCostumer> {

    val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH)
    return insuranceList.filter { insurance ->
        val regDate = insurance.regDate
        if (regDate.isNullOrEmpty()) {
            Log.d("Skipp", "Skipping the Empty dates $regDate")
            return@filter false
        }

        val parseRegDate = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(regDate)

        } catch (e: Exception) {
            Log.e("FilterDebug", "Error parsing date: $regDate", e)
            return@filter false
        }

        parseRegDate?.let {
            val reggetcalemnder = Calendar.getInstance()
            reggetcalemnder.time = it
            val regMonth = reggetcalemnder.get(Calendar.MONTH)
            Log.d(
                "FilterDebug",
                "Insurance with regDate: $regDate has parsed month: $regMonth, comparing with current month index: $currentMonthIndex"
            )
            return@filter currentMonthIndex == regMonth
        } ?: false
    }


}