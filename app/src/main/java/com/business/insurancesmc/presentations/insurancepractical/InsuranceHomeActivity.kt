package com.business.insurancesmc.presentations.insurancepractical

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.databinding.InsurancehomeBinding
import com.business.insurancesmc.presentations.adapter.InsurancAdapter
import com.business.insurancesmc.presentations.view.ImportingInsurance
import com.business.insurancesmc.presentations.viewmodel.HandlerViewModel
import com.business.insurancesmc.presentations.viewmodel.InsuranceViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.getValue
import com.business.insurancesmc.R
import com.business.insurancesmc.presentations.notification.InsuranceExpiryActivity
import com.business.insurancesmc.presentations.view.filterByCurrentByMonth
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class InsuranceHomeActivity : AppCompatActivity() {
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var binding: InsurancehomeBinding
    private lateinit var insuranceRecycler: RecyclerView
    private lateinit var insuranceAdapter: InsurancAdapter
    private val handlerViewModel: HandlerViewModel by viewModels()
    private lateinit var insuranceCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InsurancehomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermision()

        binding.addInsuranceBtn.setOnClickListener() {
            val intent = Intent(this, AddInsuranceActivity::class.java)
            startActivity(intent)
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        insuranceRecycler = binding.insuranceRecycler
        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)
        // Collect data from the flow and update the adapter when the list changes
        lifecycleScope.launch {
            viewModel.insurances.collect { insuranceList ->
                // Update the adapter with the new list of insurance items
                insuranceAdapter.submitList(insuranceList)
            }
        }

        lifecycleScope.launch() {
            viewModel.insurances.collect { insuranceList ->
                val filteredInsurances = filterByCurrentByMonth(insuranceList)
                insuranceAdapter.submitList(filteredInsurances)
            }
        }

        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)
        insuranceRecycler.adapter = insuranceAdapter
        insuranceRecycler.layoutManager = LinearLayoutManager(this)


        handlerViewModel.importStatus.observe(this) { status ->
            binding.progressText.text = status
            if (status == "Importing...") {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressText.visibility = View.VISIBLE
                Log.d("importing", "Importing in home")

            } else {
                binding.progressBar.visibility = View.GONE
                binding.progressText.visibility = View.GONE
            }
        }

        handlerViewModel.progress.observe(this) { progress ->
            binding.progressBar.progress = progress
            Log.d("importing", "Importing in home")
            binding.progressText.text = "$progress%"
        }

        insuranceCountText = binding.insuranceCountText  // Move this after 'binding' initialization

        lifecycleScope.launch {
            viewModel.insurances.collect { insuranceList ->
                val count = insuranceList.size
                Log.d("Insurance Count: ", "${insuranceList.size}")
                updateInsuranceCount(count)
                insuranceAdapter.submitList(insuranceList)

            }
        }

    }

    private fun updateInsuranceCount(count: Int) {
        // Set the text to show the current count of insurances
        insuranceCountText.text = "$count Insurances"

        // Animate the TextView if it's initially hidden
        if (insuranceCountText.visibility == View.GONE) {
            insuranceCountText.visibility = View.VISIBLE
            val fadeIn = ObjectAnimator.ofFloat(insuranceCountText, "alpha", 0f, 1f)
            fadeIn.duration = 300
            fadeIn.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.insurancemenu, menu)
        val searchItem = menu?.findItem(R.id.insurance_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search Insurance here"

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchInsurance(query)
                    val filteredList = viewModel.insurances.value
                    updateInsuranceCount(filteredList?.size ?: 0)

                }
                return true
            }

            override fun onQueryTextChange(newtext: String?): Boolean {
                newtext?.let {
                    if (newtext.isNotEmpty()) {
                        viewModel.searchInsurance(newtext)
                        val filteredList = viewModel.insurances.value
                        updateInsuranceCount(filteredList?.size ?: 0)
                    } else {
                        viewModel.getAllInsurances()
                        val allInsurances = viewModel.insurances.value
                        updateInsuranceCount(allInsurances?.size ?: 0)
                    }
                }
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {


            R.id.importdatafrom -> {
                openFilePicker()
                true
            }

            R.id.exportfromhere -> {
                val insurances = viewModel.insurances.value
                if (insurances.isNotEmpty()) {
                    choiseForExport(insurances)
                } else {
                    Toast.makeText(this, "No insurance data available", Toast.LENGTH_SHORT).show()
                }
                true
            }

            R.id.notifications -> {
                onNotificationClick()
                return true
            }

            R.id.filterList -> {
                showMonthFilterDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    fun choiseForExport(insurance: List<InsuranceCostumer>) {
        val exporter = InsuranceExportHelper(this)
        exporter.exportExcel(insurance)

    }


    fun hasExternalPermision(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermision() {
        if (hasExternalPermision()) {
            Toast.makeText(this, "Permision Already Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermison()
        }
    }

    private fun requestPermison() {
        var list = mutableListOf<String>()
        if (!hasExternalPermision()) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (list.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, list.toTypedArray(), 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permision Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permision Denied", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"  // Accept both .xls and .xlsx files
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(

                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )

            )
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Allow reading
        startActivityForResult(intent, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {

                val fileSize = getFileSize(it)
                val maxSizeMB = 3 // Set a limit (e.g., 5MB)

                if (fileSize > maxSizeMB * 1024 * 1024) {//5MB = 5 * 1024 * 1024 bytes
                    showFileTooLargeDialog()
                    return
                }

                runOnUiThread {
                    binding.progressBar.progress = 0
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressText.visibility = View.VISIBLE
                    binding.progressText.text = "Reading file..."
                    Snackbar.make(
                        binding.root, "Don't Go Back while importing!!!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val importFiles =
                            ImportingInsurance(this@InsuranceHomeActivity, handlerViewModel)
                        val insuranceUsers =
                            importFiles.extractExcelContent(uri, this@InsuranceHomeActivity)

                        withContext(Dispatchers.Main) {
                            if (insuranceUsers.isNotEmpty()) {


                                binding.progressBar.visibility = View.VISIBLE
                                binding.progressText.visibility = View.VISIBLE
                                startImport(this@InsuranceHomeActivity, insuranceUsers)
                            } else {
                                Toast.makeText(
                                    this@InsuranceHomeActivity,
                                    "No valid data found in file",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            binding.progressText.visibility = View.GONE

                            Toast.makeText(
                                this@InsuranceHomeActivity,
                                "Error reading the file: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e("ImportError", "Error importing Excel", e)
                    }
                }
            }
        }
    }

    fun startImport(context: Context, insuranceList: List<InsuranceCostumer>) {
        val filePath = saveDataToJsonFile(context, insuranceList) ?: return

        val inputData = workDataOf("FILE_PATH" to filePath)

        val workRequest = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        val workId = workRequest.id.toString() // Get correct WorkRequest ID
        Log.d("WorkManager", "WorkManager Import Task Started with ID: $workId")

        handlerViewModel.observeWorkProgress(context, workId) // Pass Correct ID
        onImportComplete(insuranceList)

    }

    fun onImportComplete(insuranceList: List<InsuranceCostumer>) {
        // Process the imported data (e.g., save to ViewModel)
        viewModel.setInsurances(insuranceList)

        // Now show the month filter dialog
        showMonthFilterDialog()
    }

    private fun saveDataToJsonFile(context: Context, data: List<InsuranceCostumer>): String? {
        return try {
            val json = Gson().toJson(data) // Convert list to JSON
            val file = File(context.filesDir, "insurance_data.json")
            file.writeText(json) // Write JSON to file
            file.absolutePath // Return file path
        } catch (e: Exception) {
            Log.e("ExportWorker", "Failed to save data: ${e.message}")
            null
        }
    }


    private fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val sizeIndex = it.getColumnIndex("_size")
            it.moveToFirst()
            it.getLong(sizeIndex)
        } ?: 0L
    }

    private fun showFileTooLargeDialog() {
        AlertDialog.Builder(this)
            .setTitle("File Too Large")
            .setMessage("The selected file is too large. Please try a smaller file (under 3MB).")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun scheduleExpiryCheck() {
        val expiryWorkRequest: WorkRequest =
            OneTimeWorkRequest.Builder(InsuranceExpiryWorker::class.java)
                .build()

        // Enqueue the worker
        WorkManager.getInstance(this).enqueue(expiryWorkRequest)
    }

    fun onNotificationClick() {
        scheduleExpiryCheck()
        val intent = Intent(this, InsuranceExpiryActivity::class.java)
        startActivity(intent)
    }

    fun showMonthFilterDialog() {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        // Display a dialog with the list of months
        android.app.AlertDialog.Builder(this)
            .setTitle("Select Month")
            .setItems(months) { _, which ->
                // Call the method to filter by the selected month
                val selectedMonth = months[which]
                filterInsurancesByMonth(selectedMonth)
            }
            .show()
    }

    fun filterInsurancesByMonth(selectedMonth: String) {
        val selectedMonthIndex = getMonthIndex(selectedMonth)
        Log.d("FilterDebug", "Selected month: $selectedMonth, Month index: $selectedMonthIndex")

        // Assuming `viewModel.insurances.value` contains the list of insurances
        val filteredInsurances = viewModel.insurances.value?.filter {
            val regDate = it.regDate
            if (regDate.isNullOrEmpty()) {
                Log.d("FilterDebug", "Skipping empty registration date for insurance: ${it.id}")
                return@filter false
            }

            // Use the correct format for dd/MM/yyyy
            val parsedRegDate = try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(regDate)
            } catch (e: ParseException) {
                Log.e("FilterDebug", "Error parsing date: $regDate", e)
                return@filter false
            }

            // If date is successfully parsed, proceed with filtering
            parsedRegDate?.let {
                val regCalendar = Calendar.getInstance()
                regCalendar.time = it
                val regMonth = regCalendar.get(Calendar.MONTH)

                // Log the parsed month to verify correctness
                Log.d(
                    "FilterDebug",
                    "Insurance with regDate: $regDate has parsed month: $regMonth, comparing with selected month index: $selectedMonthIndex"
                )

                return@filter regMonth == selectedMonthIndex
            } ?: false // If parsing failed, exclude the item
        }

        // Update the adapter with the filtered list
        filteredInsurances?.let {
            insuranceAdapter.submitList(it)
            Log.d("FilterDebug", "Filtered insurances count: ${it.size}")
            updateInsuranceCount(it.size)  // Update count after filtering
        } ?: Log.d("FilterDebug", "No insurances found after filtering.")
    }

    fun getMonthIndex(month: String): Int {
        return when (month) {
            "January" -> 0
            "February" -> 1
            "March" -> 2
            "April" -> 3
            "May" -> 4
            "June" -> 5
            "July" -> 6
            "August" -> 7
            "September" -> 8
            "October" -> 9
            "November" -> 10
            "December" -> 11
            else -> -1
        }
    }
}