package com.business.insurancesmc.presentations.view

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.presentations.viewmodel.HandlerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import java.text.DecimalFormat
import java.util.Locale

class ImportingInsurance ( private val context: Context,
private val viewModel: HandlerViewModel
) {
    suspend fun extractExcelContent(uri: Uri, applicationContext: Context): List<InsuranceCostumer> {
        return withContext(Dispatchers.IO) {
            val batchList = mutableListOf<InsuranceCostumer>()
            val batchSize = 50
            var currentBatch = mutableListOf<InsuranceCostumer>()
            val decimalFormat = DecimalFormat("#") // Fix mobile number formatting

            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    Log.e("ImportingInsurance", "Failed to open InputStream. URI: $uri")
                    showToast("Error: Could not read the file")
                    return@withContext emptyList()
                }
                inputStream.use { stream ->
                    val workbook = WorkbookFactory.create(stream)
                    val sheet = workbook.getSheetAt(0)
                    workbook.close()
                    Log.d("ExcelDebug", "Total Rows in Sheet: ${sheet.physicalNumberOfRows}")

                    for (rowIndex in 1 until sheet.physicalNumberOfRows) { // Skip header row
                        val row = sheet.getRow(rowIndex) ?: continue
                        Log.d("ExcelDebug", "Processing row: $rowIndex")

                        // Read the date properly
                        val cell = row.getCell(2) // Date column
                        val rawDate = when {
                            cell == null -> ""
                            cell.cellType == CellType.NUMERIC -> {
                                try {
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cell.dateCellValue)
                                } catch (e: Exception) {
                                    Log.e("ExcelDebug", "Date parsing error at row $rowIndex: ${e.message}")
                                    ""
                                }
                            }
                            else -> cell.toString().trim()
                        }
                        val cell1 = row.getCell(16) // Date column
                        val  expipyDate1 = when {
                            cell1 == null -> ""
                            cell1.cellType == CellType.NUMERIC -> {
                                try {
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cell1.dateCellValue)
                                } catch (e: Exception) {
                                    Log.e("ExcelDebug", "Date parsing error at row $rowIndex: ${e.message}")
                                    ""
                                }
                            }
                            else -> cell.toString().trim()
                        }

                        // Read and format mobile number
                        val mobileCell = row.getCell(14)
                        val mobile = when (mobileCell?.cellType) {
                            CellType.NUMERIC -> decimalFormat.format(mobileCell.numericCellValue)
                            CellType.STRING -> mobileCell.stringCellValue.trim()
                            else -> "0"
                        }
//                        val ownerFamilyCell=row.getCell(4)
//                        val ownerFamily=if(ownerFamilyCell!=null) ownerFamilyCell.toString().trim() else ""

                        val statusCell=row.getCell(15)
                        val statusThing=if(statusCell!=null) statusCell.toString().trim() else ""
                        val insurance = InsuranceCostumer(
                            state = row.getCell(0)?.toString()?.trim() ?: "",
                            regNo = row.getCell(1)?.toString()?.trim() ?: "",
                            regDate = rawDate,
                            ownerName = row.getCell(3)?.toString()?.trim() ?: "",
                            ownerFamily = row.getCell(4)?.toString()?.trim() ?: "",
                            address = row.getCell(5)?.toString()?.trim() ?: "",
                            enginNo = row.getCell(6)?.toString()?.trim() ?: "",
                            chasNo = row.getCell(7)?.toString()?.trim() ?: "",
                            vehicleMake = row.getCell(8)?.toString()?.trim() ?: "",
                            vehicleModel = row.getCell(9)?.toString()?.trim() ?: "",
                            vehicleClass = row.getCell(10)?.toString()?.trim() ?: "",
                            fuel = row.getCell(11)?.toString()?.trim() ?: "",
                            saleAmount = row.getCell(12)?.toString()?.trim() ?: "",
                            seatCapacity = row.getCell(13)?.toString()?.trim() ?: "",
                            mobile = mobile.toString().toLong(),
                            status =statusThing,
                            expiryDate = expipyDate1

                        )

                        if (mobile != "0" && (insurance.status.isNotEmpty() || insurance.status.isEmpty())) {
                            Log.d("ExcelDebug", "Row $rowIndex - Extracted mobile: $mobile")
                            currentBatch.add(insurance)
                        }
                        Log.d("ExcelDebug", "Row $rowIndex - Extracted values: State=${row.getCell(0)}, RegNo=${row.getCell(1)}, Mobile=$mobile")
                        Log.d("ExcelDebug", "Row $rowIndex - Extracted values: RegDate=${row.getCell(2)}, Owner=${row.getCell(3)}, Address=${row.getCell(4)}")

                        if (currentBatch.size >= batchSize) {
                            batchList.addAll(currentBatch)
                            currentBatch.clear()

                            Log.d("ExcelDebug", "Processing batch of ${batchList.size} records...")
                            delay(1) // Prevents blocking
                        }
                    }

                    if (currentBatch.isNotEmpty()) {
                        batchList.addAll(currentBatch)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error importing Excel: ${e.message}")
                }
                Log.e("ExcelDebug", "Exception: ${e.message}")
                e.printStackTrace()
            }

            Log.d("ExcelDebug", "Total valid insurance records: ${batchList.size}")
            batchList
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
