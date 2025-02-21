package com.business.insurancesmc.presentations.otpverify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.business.insurancesmc.R
import com.business.insurancesmc.presentations.insurancepractical.InsuranceHomeActivity

class OtpActivity : AppCompatActivity() {

    private var generatedOtp: String = "" // This will hold the generated OTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val sendOtpButton: Button = findViewById(R.id.sendOtpButton)
        val verifyOtpButton: Button = findViewById(R.id.verifyOtpButton)
        val otpEditText: EditText = findViewById(R.id.otpEditText)

        // Send OTP when the user clicks the 'Send OTP' button
        sendOtpButton.setOnClickListener {
            generatedOtp = generateOtp() // Generate a new OTP
            sendOtpNotification(this, generatedOtp) // Send OTP via notification
            Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()

            // Animate the Send OTP button to move up and disappear
            sendOtpButton.animate()
                .translationY(-100f) // Move up by 100 pixels
                .alpha(0f) // Fade out
                .setDuration(700) // Duration for the animation
                .withEndAction {
                    sendOtpButton.isVisible =false
                }

            // Animate the EditText and Verify OTP button to appear slowly
            otpEditText.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f) // Fade in
                    .setDuration(1200) // Duration for the fade-in animation
            }

            verifyOtpButton.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f) // Fade in
                    .setDuration(1300) // Duration for the fade-in animation
            }
        }

        // Verify OTP when the user clicks the 'Verify OTP' button
        verifyOtpButton.setOnClickListener {
            val enteredOtp = otpEditText.text.toString()
            if (verifyOtp(enteredOtp, generatedOtp)) {
                val intent = Intent(this, InsuranceHomeActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Generate OTP function
    fun generateOtp(): String {
        val otp = (100000..999999).random() // Generate a 6-digit OTP
        return otp.toString()
    }

    // Verify OTP function
    fun verifyOtp(inputOtp: String, correctOtp: String): Boolean {
        return inputOtp == correctOtp
    }
}
