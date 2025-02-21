package com.business.insurancesmc.presentations.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.databinding.InsuranceexpiryactivityBinding
import com.business.insurancesmc.presentations.adapter.InsurancAdapter
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class InsuranceExpiryActivity : AppCompatActivity() {

    private lateinit var viewModel: InsuranceViewModel
    private lateinit var binding: InsuranceexpiryactivityBinding
    private lateinit var insuranceAdapter: InsurancAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InsuranceexpiryactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the notification type (Expiring Soon or Expired)
        val notificationType = intent.getStringExtra("notification_type")

        viewModel = ViewModelProvider(this).get(InsuranceViewModel::class.java)

        // Filter the insurances based on the notification type
        val filteredList = when (notificationType) {
            "Expiring Soon" -> {
                filterExpiringSoonInsurances()
            }
            "Expired" -> {
                filterExpiredInsurances()
            }
            else -> mutableListOf() // Handle default case
        }

        // Set up RecyclerView with the filtered data
        insuranceAdapter = InsurancAdapter(filteredList, viewModel)
        binding.recyclerView.adapter = insuranceAdapter
    }

    // Method to filter expiring soon insurances
    private fun filterExpiringSoonInsurances(): MutableList<InsuranceCostumer> {
        val currentDate = Calendar.getInstance().time
        return viewModel.insurances.value?.filterTo(mutableListOf()) { insurance ->
            val expiryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(insurance.regDate)
            expiryDate?.let {
                it.after(currentDate) && it.before(Calendar.getInstance().apply { add(Calendar.DATE, 7) }.time)
            } ?: false
        } ?: mutableListOf()
    }

    // Method to filter expired insurances
    private fun filterExpiredInsurances(): MutableList<InsuranceCostumer> {
        val currentDate = Calendar.getInstance().time
        return viewModel.insurances.value?.filterTo(mutableListOf()) { insurance ->
            val expiryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(insurance.regDate)
            expiryDate?.before(currentDate) ?: false
        } ?: mutableListOf()
    }
}
