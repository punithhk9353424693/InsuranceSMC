import android.content.Context
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import com.business.insurancesmc.R
import com.business.insurancesmc.data.repo.InsurancePerson
import com.business.insurancesmc.data.model.InsuranceCostumer
import com.business.insurancesmc.presentations.notification.InsuranceExpiryActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltWorker
class InsuranceExpiryWorker @Inject constructor(
    @ApplicationContext  context: Context,
    workerParams: WorkerParameters,
    private val insuranceRepo: InsurancePerson // Injecting repository via Hilt
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            // Collect the flow to get a List of InsuranceCostumer
            val insurances: List<InsuranceCostumer> = insuranceRepo.getAllInsurance().first()

            val expiringSoon = insurances.filter {
                val expiryDate = dateFormat.parse(it.expiryDate) ?: return@filter false
                val diff = expiryDate.time - currentDate.timeInMillis
                diff in 0..(7 * 24 * 60 * 60 * 1000) // Expiring within 7 days
            }

            val expired = insurances.filter {
                val expiryDate = dateFormat.parse(it.expiryDate) ?: return@filter false
                val diff = currentDate.timeInMillis - expiryDate.time
                diff > 0 // Already expired
            }
            android.util.Log.d("InsuranceExpiryWorker", "Expiring soon: ${expiringSoon.size}")
            android.util.Log.d("InsuranceExpiryWorker", "Expired: ${expired.size}")

            // Show notifications if there are expiring soon or expired insurances
            if (expiringSoon.isNotEmpty()) {
                showNotification("Expiring Soon", expiringSoon.size)
            }

            if (expired.isNotEmpty()) {
                showNotification("Expired", expired.size)
            }

            Result.success()
        } catch (e: Exception) {
            // Handle error gracefully and return failure
            Result.failure()
        }
    }

    private fun showNotification(type: String, count: Int) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to launch InsuranceExpiryActivity when the notification is clicked
        val intent = Intent(applicationContext, InsuranceExpiryActivity::class.java).apply {
            putExtra("notification_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "insurance_expiry_channel", "Insurance Expiry Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification =
            NotificationCompat.Builder(applicationContext, "insurance_expiry_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("$type Notification")
                .setContentText("$count insurance(s) $type!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)  // Allow dismiss on click
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(1, notification)
    }
}
