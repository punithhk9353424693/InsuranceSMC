package com.business.insurancesmc.presentations.insurancepractical

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.business.insurancesmc.R
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.databinding.InsurancecostumerdetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsuranceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: InsurancecostumerdetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = InsurancecostumerdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the InsuranceCostumer object from the intent
        val insurance = intent.getParcelableExtra<InsuranceCostumer>("insuranceDetails")

        insurance?.let {
            // Bind the data to the views
            bindInsuranceDetails(it)

            // Apply underline to mobile number TextView
            val spannable = SpannableString(binding.tvMobile.text)
            spannable.setSpan(
                UnderlineSpan(),
                0,
                spannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvMobile.text = spannable

            // Set mobile number click listener
            binding.tvMobile.setOnClickListener {
                val mobileText = binding.tvMobile.text.toString()
                val mobileNumber = mobileText.replace("Mobile: ", "").trim()
                Log.d("mobile No", "Mobile number is: $mobileNumber")

                Log.d("mobile No", "$mobileNumber")
                if (mobileNumber.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobileNumber"))
                    startActivity(intent)
                }
            }
        }
        val copyButton = findViewById<ImageButton>(R.id.copyButton)
        copyButton.setOnClickListener {
            copyAllTextToClipboard()
        }


    }

    private fun bindInsuranceDetails(insurance: InsuranceCostumer) {
        // Set the header details
        binding.tvOwnerName.text = insurance.ownerName.capitalizeFirstLetter()
        binding.tvMobile.text = "Mobile: ${insurance.mobile}"

        binding.tvVehicleDetails.text =
            "Vehicle: ${insurance.vehicleMake.capitalizeFirstLetter()} - ${insurance.vehicleModel.capitalizeFirstLetter()} (${insurance.vehicleClass.capitalizeFirstLetter()})"

        binding.tvOwnerFamily.text =
            "Owner Family: ${insurance.ownerFamily.capitalizeFirstLetter()}"
        // Set the detailed information
        binding.tvState.text = "State: ${insurance.state.capitalizeFirstLetter()}"
        binding.tvRegNo.text = "Registration No: ${insurance.regNo?.capitalizeFirstLetter()}"
        binding.tvRegDate.text = "Registration Date: ${insurance.regDate}"
        binding.tvAddress.text = "Address: ${insurance.address?.capitalizeFirstLetter()}"
        binding.tvEngineNo.text = "Engine No: ${insurance.enginNo?.capitalizeFirstLetter()}"
        binding.tvChassisNo.text = "Chassis No: ${insurance.chasNo?.capitalizeFirstLetter()}"
        binding.tvVehicleMake.text =
            "Vehicle Make: ${insurance.vehicleMake.capitalizeFirstLetter()}"
        binding.tvVehicleModel.text =
            "Vehicle Model: ${insurance.vehicleModel.capitalizeFirstLetter()}"
        binding.tvFuel.text = "Fuel Type: ${insurance.fuel.capitalizeFirstLetter()}"
        binding.tvSaleAmount.text = "Sale Amount: ${insurance.saleAmount}"
        binding.tvSeatCapacity.text = "Seat Capacity: ${insurance.seatCapacity?.lowercase()}"
        binding.tvStatus.text = "Status: ${insurance.status?.capitalizeFirstLetter()}"
        binding.tvRegDate.text = "Expiry Date: ${insurance.expiryDate}"
    }

    fun String.capitalizeFirstLetter(): String {
        return this.lowercase().replaceFirstChar { it.titlecase() }
    }

    private fun copyAllTextToClipboard() {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val textToCopy = buildString {
            append(binding.tvOwnerName.text)
            append("\n")
            append(binding.tvMobile.text)
            append("\n")
            append(binding.tvVehicleDetails.text)
            append("\n")
            append(binding.tvOwnerFamily.text)
            append("\n")
            append(binding.tvState.text)
            append("\n")
            append(binding.tvRegNo.text)
            append("\n")
            append(binding.tvRegDate.text)
            append("\n")
            append(binding.tvAddress.text)
            append("\n")
            append(binding.tvEngineNo.text)
            append("\n")
            append(binding.tvChassisNo.text)
            append("\n")
            append(binding.tvVehicleMake.text)
            append("\n")
            append(binding.tvVehicleModel.text)
            append("\n")
            append(binding.tvFuel.text)
            append("\n")
            append(binding.tvSaleAmount.text)
            append("\n")
            append(binding.tvSeatCapacity.text)
            append("\n")
            append(binding.tvStatus.text)
        }

        // Copy the built string to clipboard
        val clip = android.content.ClipData.newPlainText("Insurance Details", textToCopy)
        clipboard.setPrimaryClip(clip)
    }
}