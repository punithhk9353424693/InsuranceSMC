package com.business.insurancesmc.presentations.insurancepractical

import android.os.Bundle
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.databinding.AddinginsuranceBinding
import com.business.insurancesmc.presentations.adapter.InsurancAdapter
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.getValue
@AndroidEntryPoint
class AddInsuranceActivity: AppCompatActivity() {
    private lateinit var binding: AddinginsuranceBinding
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var insuranceAdapter: InsurancAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddinginsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)

        val currentDate = Calendar.getInstance()
        val dateFormat= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatDate=dateFormat.format(currentDate.time)
        binding.regDateEditText.setText(formatDate)


        val expiryDate=calculateExpiryDate(formatDate)
        binding.expiryDateEditText.setText(expiryDate)

        binding.backwordadd.setOnClickListener() {
            finish()
        }
        binding.statusEditText.setOnClickListener(){
            showMenu(binding.statusEditText)
        }
        binding.submitButton.setOnClickListener() { view ->
            val state = binding.stateEditText.text.toString()
            val regNumber = binding.regNoEditText.text.toString()
            val regDate = binding.regDateEditText.text.toString()
            val ownerName = binding.ownerNameEditText.text.toString()
            val ownerfamily=binding.ownerFamilyEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val engineNo = binding.enginNoEditText.text.toString()
            val chassisNo = binding.chasNoEditText.text.toString()
            val vehicleMake = binding.vehicleMakeEditText.text.toString()
            val vehicleModel = binding.vehicleModelEditText.text.toString()
            val vehicleClass = binding.vehicleClassEditText.text.toString()
            val fuel = binding.fuelEditText.text.toString()
            val saleAmount = binding.saleAmountEditText.text.toString()
            val seatCapacity = binding.seatCapacityEditText.text.toString()
            val mobileNo = binding.mobileEditText.text.toString()
            val status=binding.statusEditText.text.toString()

            if (state.isNotEmpty() && regNumber.isNotEmpty() && regDate.isNotEmpty()
                && ownerName.isNotEmpty() && address.isNotEmpty() && engineNo.isNotEmpty() && chassisNo.isNotEmpty() &&
                vehicleMake.isNotEmpty() && vehicleModel.isNotEmpty() && vehicleClass.isNotEmpty() &&
                fuel.isNotEmpty() && saleAmount.isNotEmpty() && seatCapacity.isNotEmpty() && mobileNo.isNotEmpty()
            ) {
                val insurance = InsuranceCostumer(
                    state = state,
                    regNo = regNumber,
                    regDate = regDate,
                    ownerName = ownerName,
                    ownerFamily = ownerfamily,
                    address = address,
                    enginNo = engineNo,
                    chasNo = chassisNo,
                    vehicleMake = vehicleMake,
                    vehicleModel = vehicleModel,
                    vehicleClass = vehicleClass,
                    fuel = fuel,
                    saleAmount = saleAmount,
                    seatCapacity = seatCapacity,
                    mobile = mobileNo.toLong(),
                    status = status,
                    expiryDate = expiryDate
                )
                viewModel.viewModelScope.launch {
                    viewModel.insertInsurance(insurance)
                    insuranceAdapter.addInsurance(insurance)

                }

                finish()

                Snackbar.make(view, "Insurance added Successfully ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Fill all the fields with proper inputs ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
    fun showMenu(status: EditText){
        val popupMenu= PopupMenu(this,status)
        val menu=popupMenu.menu
        menu.add("Open")
        menu.add("Progress")
        menu.add("Done")
        popupMenu.setOnMenuItemClickListener(){item->
            status.setText(item.title)
            true
        }
        popupMenu.show()

    }

    private fun calculateExpiryDate(regDate: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        try {
            val regDateParsed = dateFormat.parse(regDate)
            regDateParsed?.let {
                calendar.time = it
                calendar.add(Calendar.YEAR, 1) // Add one year to registration date for expiry
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dateFormat.format(calendar.time) // Return the formatted expiry date
    }

}




