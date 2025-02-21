package com.business.insurancesmc.presentations.otpverify
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.business.insurancesmc.R

fun sendOtpNotification(context: Context, otp: String) {
    // Create Notification Channel (required for Android 8.0 and above)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "otp_channel"
        val channelName = "OTP Notifications"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val intent=Intent(context, OtpActivity::class.java)
    val stackBuilder= TaskStackBuilder.create(context)
    stackBuilder.addParentStack(OtpActivity::class.java)
    stackBuilder.addNextIntent(intent)
    val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

    // Build the notification
    val notification = NotificationCompat.Builder(context, "otp_channel")
        .setSmallIcon(R.drawable.otpicon) // Replace with your icon
        .setContentTitle("Your OTP Code")
        .setContentText("Your OTP code is: $otp")
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    // Send the notification
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(0, notification)
}
