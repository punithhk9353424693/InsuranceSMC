package com.business.insurancesmc.presentations.insurancepractical

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.widget.EditText
import android.widget.Toast
import com.business.insurancesmc.data.model.InsuranceCostumer
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class InsuranceExportHelper (private val context: Context) {
    fun exportExcel(customers: List<InsuranceCostumer>) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.hint = "Enter Excel File Name"
        builder.setTitle("Enter Excel File Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val fileName = input.text.toString().trim()
                if (fileName.isNotEmpty() && isValidFileName(fileName)) {
                    saveExcelToStorage(fileName, customers)
                } else {
                    // Show a Toast message if the file name is empty or invalid
                    if (fileName.isEmpty()) {
                        Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Invalid file name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    fun isValidFileName(fileName: String): Boolean {
        val illegalFileName =
            listOf("/", "\\", "?", ">", "<", "|", "*", ":", ";", "@", "%", "^", "(", ")")
        return illegalFileName.none { fileName.contains(it) }
    }

    fun saveExcelToStorage(fileName: String, customers: List<InsuranceCostumer>) {
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            try {
                val workbook: Workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Insurance Customer Data")
                val headerRow = sheet.createRow(0)


                headerRow.createCell(0).setCellValue("STATE")
                headerRow.createCell(1).setCellValue("REG NO")
                headerRow.createCell(2).setCellValue("REG DATE")
                headerRow.createCell(3).setCellValue("OWNER NAME")
                headerRow.createCell(4).setCellValue("OWNER(S/W/D)")
                headerRow.createCell(5).setCellValue("ADDRESS")
                headerRow.createCell(6).setCellValue("ENGINE NO")
                headerRow.createCell(7).setCellValue("CHASIS NO")
                headerRow.createCell(8).setCellValue("VEHICLE MAKE")
                headerRow.createCell(9).setCellValue("VEHICLE MODEL VARIANT")
                headerRow.createCell(10).setCellValue("VEHICLE CLASS")
                headerRow.createCell(11).setCellValue("FUEL")
                headerRow.createCell(12).setCellValue("SALE AMOUNT")
                headerRow.createCell(13).setCellValue("SEAT CAPACITY")
                headerRow.createCell(14).setCellValue("MOBILE")
                headerRow.createCell(15).setCellValue("Expiry Date")
                headerRow.createCell(16).setCellValue("STATUS")


                customers.forEachIndexed { index, insurance ->
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(insurance.state)
                    row.createCell(1).setCellValue(insurance.regNo)
                    row.createCell(2).setCellValue(insurance.regDate)
                    row.createCell(3).setCellValue(insurance.ownerName)
                    row.createCell(4).setCellValue(insurance.ownerFamily)
                    row.createCell(5).setCellValue(insurance.address)
                    row.createCell(6).setCellValue(insurance.enginNo)
                    row.createCell(7).setCellValue(insurance.chasNo)
                    row.createCell(8).setCellValue(insurance.vehicleMake)
                    row.createCell(9).setCellValue(insurance.vehicleModel)
                    row.createCell(10).setCellValue(insurance.vehicleClass)
                    row.createCell(11).setCellValue(insurance.fuel)
                    row.createCell(12).setCellValue(insurance.saleAmount)
                    row.createCell(13).setCellValue(insurance.seatCapacity)
                    row.createCell(14).setCellValue(insurance.mobile.toString())
                    row.createCell(15).setCellValue(insurance.expiryDate.toString())
                    row.createCell(16).setCellValue(insurance.status)
                }


                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (downloadDir == null || !downloadDir.exists()) {
                    downloadDir.mkdirs()
                }
                val excelFile = File(downloadDir, "$fileName.xlsx")
                val fileOut = FileOutputStream(excelFile)
                workbook.write(fileOut)
                fileOut.close()
                workbook.close()
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Excel file saved at: ${excelFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()


                }
            } catch (e: Exception) {
                e.printStackTrace()
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "Error exporting to Excel", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}