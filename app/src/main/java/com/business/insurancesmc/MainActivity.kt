package com.business.insurancesmc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.business.insurancesmc.presentations.insurancepractical.InsuranceHomeActivity
import com.business.insurancesmc.presentations.otpverify.OtpActivity
import com.business.insurancesmc.ui.theme.InsuranceSMCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        // Get the views to animate
        val logoImageView: ImageView = findViewById(R.id.splash_logo)
        val splashTextView: TextView = findViewById(R.id.splash_text)
        val subTitleTextView: TextView = findViewById(R.id.splash_subtitle)  // Correct reference

        // Set fade-in animation for the logo
        val fadeInLogo = AlphaAnimation(0f, 1f)
        fadeInLogo.duration = 1500  // 1.5 seconds fade-in
        logoImageView.startAnimation(fadeInLogo)

        // Set fade-in animation for the text
        val fadeInText = AlphaAnimation(0f, 1f)
        fadeInText.duration = 1500  // 1.5 seconds fade-in
        fadeInText.startOffset = 1500  // Wait for the logo to finish
        splashTextView.visibility = TextView.VISIBLE
        splashTextView.startAnimation(fadeInText)

        // Set fade-in animation for the subtitle
        val fadeInSubTitle = AlphaAnimation(0f, 1f)
        fadeInSubTitle.duration = 1500  // 1.5 seconds fade-in
        fadeInSubTitle.startOffset = 2500  // Delay it after text animation
        subTitleTextView.visibility = TextView.VISIBLE
        subTitleTextView.startAnimation(fadeInSubTitle)

        // Transition to the next screen after a delay (4 seconds in total)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InsuranceHomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000) // 4 seconds splash screen duration
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InsuranceSMCTheme {
        Greeting("Android")
    }
}