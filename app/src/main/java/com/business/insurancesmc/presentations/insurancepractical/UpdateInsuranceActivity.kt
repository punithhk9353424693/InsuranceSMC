package com.business.insurancesmc.presentations.insurancepractical

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.databinding.UpdateinsuranceBinding
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
class UpdateInsuranceActivity : AppCompatActivity() {
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var binding: UpdateinsuranceBinding
    private lateinit var insuranceAdapter: InsurancAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdateinsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insurance = intent.getParcelableExtra<InsuranceCostumer>("insuranceDetails")
        insurance?.let {
            binding.etState.setText(insurance.state)
            binding.etRegNo.setText(insurance.regNo)
            binding.etRegDate.setText(insurance.regDate)
            binding.etOwnerName.setText(insurance.ownerName)
            binding.etOwnerfamily.setText(insurance.ownerFamily)
            binding.etAddress.setText(insurance.address)
            binding.etEngineNo.setText(insurance.enginNo)
            binding.etChassisNo.setText(insurance.chasNo)
            binding.etVehicleMake.setText(insurance.vehicleMake)
            binding.etVehicleModel.setText(insurance.vehicleModel)
            binding.etVehicleClass.setText(insurance.vehicleClass)
            binding.etFuel.setText(insurance.fuel)
            binding.etSaleAmount.setText(insurance.saleAmount)
            binding.etSeatCapacity.setText(insurance.seatCapacity)
            binding.etMobile.setText(insurance.mobile.toString())
            binding.etStatus.setText(insurance.status)
            binding.etExpiryDate.setText(insurance.expiryDate)

        }

        insuranceAdapter= InsurancAdapter(mutableListOf(),viewModel)

        binding.etStatus.setOnClickListener() {
            showStatusMenu(binding.etStatus)
        }
        binding.etExpiryDate.setOnClickListener() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etExpiryDate.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        binding.backupdate.setOnClickListener() {
            finish()
        }
        val dateString = binding.etRegDate.text.toString().trim()

        if (dateString.isNotEmpty()) {

            try {
                val dateFormate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val parsedDate = dateFormate.parse(dateString)
                if(parsedDate!=null){
                    val formattedDate=dateFormate.format(parsedDate)
                    val checkExpiry=checkExpiryDate(formattedDate)
                    binding.etExpiryDate.setText(checkExpiry)  // Set formatted date to the expiry date field

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }else{
            Toast.makeText(this,"The date field is empty", Toast.LENGTH_SHORT).show()
        }
        binding.etRegDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etRegDate.setText(selectedDate)
                    val formattedExpiryDate = checkExpiryDate(selectedDate)
                    binding.etExpiryDate.setText(formattedExpiryDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        binding.btnUpdate.setOnClickListener { view ->
            insurance?.let {
                // Update the insurance object with new values from the UI
                it.state = binding.etState.text.toString()
                it.regNo = binding.etRegNo.text.toString()
                it.regDate = binding.etRegDate.text.toString()
                it.ownerName = binding.etOwnerName.text.toString()
                it.ownerFamily = binding.etOwnerfamily.text.toString()
                it.address = binding.etAddress.text.toString()
                it.enginNo = binding.etEngineNo.text.toString()
                it.chasNo = binding.etChassisNo.text.toString()
                it.vehicleMake = binding.etVehicleMake.text.toString()
                it.vehicleModel = binding.etVehicleModel.text.toString()
                it.vehicleClass = binding.etVehicleClass.text.toString()
                it.fuel = binding.etFuel.text.toString()
                it.saleAmount = binding.etSaleAmount.text.toString()
                it.seatCapacity = binding.etSeatCapacity.text.toString()
                it.mobile = binding.etMobile.text.toString().toLong()
                it.status = binding.etStatus.text.toString()
                it.expiryDate=binding.etExpiryDate.text.toString()
                // Call the ViewModel to update the insurance
                viewModel.viewModelScope.launch {
                    viewModel.updateInsurance(it)

                    viewModel.getSortedInsuranceList().collect{insurances->
                        insuranceAdapter.submitList(insurances)
                    }
                }


                Snackbar.make(view, "Details Updated Successfully", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                Log.d("Debug", "OwnerFamily: ${insurance.ownerFamily}, Status: ${insurance.status}")

                finish()
            }
        }
    }

    fun showStatusMenu(status: EditText) {
        val popupMenu = PopupMenu(this, status)
        val menu = popupMenu.menu
        menu.add("Open")
        menu.add("Progress")
        menu.add("Done")

        popupMenu.setOnMenuItemClickListener { item ->
            status.setText(item.title)
            true
        }
        popupMenu.show()


    }

    fun checkExpiryDate(regDate: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calender = Calendar.getInstance()
        try {
            val parseDate = dateFormat.parse(regDate)
            parseDate?.let {
                calender.time = it
                calender.add(Calendar.YEAR, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dateFormat.format(calender.time) // Return the formatted expiry date
    }
}
