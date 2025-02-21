package com.business.insurancesmc.presentations.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.presentations.insurancepractical.InsuranceDetailsActivity
import com.business.insurancesmc.presentations.insurancepractical.UpdateInsuranceActivity
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.business.insurancesmc.R

class InsurancAdapter(
    private var insurances: MutableList<InsuranceCostumer>,
    val viewModel: InsuranceViewModel
) : RecyclerView.Adapter<InsurancAdapter.InsuranceViewHolder>() {
    inner class InsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ownerName: TextView = itemView.findViewById(R.id.ownerName)
        val mobileNo: TextView = itemView.findViewById(R.id.mobileNo)
        val vehicleType: TextView = itemView.findViewById(R.id.Vehicletype)
        val phonecal: ImageButton = itemView.findViewById(R.id.phonecallInsurance)
        val edit: ImageButton = itemView.findViewById(R.id.editInsurance)
        val delete: ImageButton = itemView.findViewById(R.id.deleteinsurance)
        val viewEye: ImageButton = itemView.findViewById(R.id.viewEyebtnInsurance)
        val messageInsurance: ImageButton = itemView.findViewById(R.id.messageInsurance)
        val regdate: TextView = itemView.findViewById(R.id.regdate)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val liniarCard: LinearLayout = itemView.findViewById(R.id.lineiarcard)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InsuranceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.insuranceadapterlayout, parent, false)
        return InsuranceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: InsuranceViewHolder,
        position: Int
    ) {
        val insurance = insurances[position]
        holder.ownerName.text = insurance.ownerName
        holder.mobileNo.text = insurance.mobile.toString()
        holder.vehicleType.text = insurance.vehicleClass
        holder.regdate.text = insurance.regDate
        val context = holder.itemView.context

        holder.phonecal.setOnClickListener() {
            callInsurancePerson(holder.itemView.context, insurance.mobile.toString())
        }

        holder.messageInsurance.setOnClickListener {
            val phono = holder.mobileNo.text.toString()
            val ownername = holder.ownerName.text.toString()
            if (phono.isNotEmpty()) {
                openWhatsAppChat(holder.itemView.context, phono, ownername) // Pass the phone number
            } else {
                Toast.makeText(context, "Phone number is empty", Toast.LENGTH_SHORT).show()
            }
        }
        holder.delete.setOnClickListener {
            val deletedCustomer = insurances[position]
            val deletePosition = position

            // Remove the item from the list
            insurances.removeAt(deletePosition)
            notifyItemRemoved(deletePosition)

            // Show Snackbar with Undo option
            val snackbar = Snackbar.make(holder.itemView, "Customer deleted", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // Restore the deleted item
                insurances.add(deletePosition, deletedCustomer)
                notifyItemInserted(deletePosition)
                viewModel.viewModelScope.launch() {
                    viewModel.insertInsurance(deletedCustomer) // Reinsert into the database
                }
            }
            snackbar.show()

            // Delete from the database
            viewModel.deleteInsurance(deletedCustomer.id)

        }
        holder.itemView.setOnClickListener() {
            val intent = Intent(context, InsuranceDetailsActivity::class.java).apply {
                putExtra("insuranceDetails", insurance) // Pass the InsuranceCostumer object
            }
            context.startActivity(intent)
        }
        holder.viewEye.setOnClickListener {
            val intent = Intent(context, InsuranceDetailsActivity::class.java).apply {
                putExtra("insuranceDetails", insurance) // Pass the InsuranceCostumer object
            }
            context.startActivity(intent)
        }
        holder.edit.setOnClickListener {
            val intent = Intent(context, UpdateInsuranceActivity::class.java).apply {
                putExtra("insuranceDetails", insurance) // Pass the InsuranceCostumer object
            }
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            // Show the share button (you can make it visible here)
            holder.itemView.findViewById<ImageButton>(R.id.shareInsurance).visibility = View.VISIBLE
            true // Return true to indicate the event has been handled
        }
        //sharing information
        holder.itemView.findViewById<ImageButton>(R.id.shareInsurance).setOnClickListener {
            // Create a message with all insurance details
            val messageToShare = """
            Insurance Details:
            State : ${insurance.state}
            Owner Name: ${insurance.ownerName}
            Owner_Family: ${insurance.ownerFamily}
            Mobile Number: ${insurance.mobile}
             Address: ${insurance.address}
            Vehicle Type: ${insurance.vehicleClass}
            Registration Date: ${insurance.regDate}
             SeatCapacity: ${insurance.seatCapacity}
            Fuel: ${insurance.fuel}
            Registration Number: ${insurance.regNo}
            Sale_Amount: ${insurance.saleAmount}
            Chassis_Number: ${insurance.chasNo}
            Policy Expiry Date: ${insurance.expiryDate}
            Engine_No Insured: ${insurance.enginNo}
             Status : ${insurance.status}

            *Contact Sri Maruthi Consultancy for more info.*
             You can reach us at:
                 📞 *9886130584*
                 📞 *7338171202*
        """.trimIndent()

            // Create the share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, messageToShare)
                type = "text/plain"
            }

            // Start the share intent
            context.startActivity(Intent.createChooser(shareIntent, "Share Insurance Details"))
        }

        holder.status.text = insurance.status
        when (insurance.status) {
            "Open" -> holder.liniarCard.setBackgroundColor(R.drawable.insuranceforcardbackground)
            "Progress" -> holder.liniarCard.setBackgroundColor(R.drawable.insuranceforcardbackground)
            "Done" -> holder.liniarCard.setBackgroundColor(android.graphics.Color.GREEN)
            else -> holder.liniarCard.setBackgroundColor(R.drawable.insuranceforcardbackground)
        }


    }


    override fun getItemCount(): Int {
        return insurances.size
    }

    fun addInsurance(newInsurance: InsuranceCostumer) {
        this.insurances.add(newInsurance)
        notifyItemInserted(insurances.size - 1)  // Notify the adapter that a new item has been added
    }

    fun submitList(insuranceList: List<InsuranceCostumer>) {
        this.insurances = insuranceList.toMutableList()
        notifyDataSetChanged()

    }


    fun callInsurancePerson(context: Context, mobile: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile"))
        context.startActivity(intent)

    }

    fun openWhatsAppChat(context: Context, phono: String, ownerName: String) {
        try {
            // Ensure the phone number starts with a '+' and includes the country code
            val cleanedPhoneNumber = if (phono.startsWith("+")) {
                phono.replace("[^0-9+]".toRegex(), "") // Remove unwanted characters
            } else {
                "+$phono".replace("[^0-9+]".toRegex(), "") // Add '+' if missing
            }
            val message = """
                Dear $ownerName,
                 Greetings from Sri Maruthi Consultancy. Vehicle insurance.
                 More than 38 insurance companies and government insurance companies.

                 *We give the cashback offer!* 🎉
            
                 You can reach us at:
                 📞 *9886130584*
                 📞 *7338171202*
               """.trimIndent()
            // Check if the phone number is valid (at least 10 digits including country code)
            if (cleanedPhoneNumber.length >= 10) {
                val encodeMessage = Uri.encode(message)
                val uri = Uri.parse("https://wa.me/$cleanedPhoneNumber?text=$encodeMessage")

                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.whatsapp")
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp Not Installed", Toast.LENGTH_SHORT).show()
        }

    }

}