package com.example.daysuntilidie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val birthdateInput = findViewById<EditText>(R.id.birthdateInput)
        val expectedAgeInput = findViewById<EditText>(R.id.expectedAgeInput)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultText = findViewById<TextView>(R.id.resultText)

        // SharedPreferences for persistence
        val sharedPreferences = getSharedPreferences("DaysUntilIDiePrefs", MODE_PRIVATE)

        // Load saved data
        birthdateInput.setText(sharedPreferences.getString("birthdate", ""))
        expectedAgeInput.setText(sharedPreferences.getString("expectedAge", ""))
        resultText.text = sharedPreferences.getString("result", "Days left: ")

        calculateButton.setOnClickListener {
            val birthdateString = birthdateInput.text.toString()
            val expectedAgeString = expectedAgeInput.text.toString()

            if (birthdateString.isNotEmpty() && expectedAgeString.isNotEmpty()) {
                try {
                    // Parse inputs
                    val birthdate = LocalDate.parse(birthdateString, DateTimeFormatter.ISO_DATE)
                    val expectedAge = expectedAgeString.toInt()

                    // Calculate estimated death date
                    val deathDate = birthdate.plusYears(expectedAge.toLong())

                    // Calculate days left
                    val today = LocalDate.now()
                    val daysLeft = ChronoUnit.DAYS.between(today, deathDate)

                    // Update UI
                    val result = if (daysLeft >= 0) {
                        "Days left: $daysLeft"
                    } else {
                        "You're already ${-daysLeft / 365} years past your expected lifespan!"
                    }
                    resultText.text = result

                    // Save to SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.putString("birthdate", birthdateString)
                    editor.putString("expectedAge", expectedAgeString)
                    editor.putString("result", result)
                    editor.apply()
                } catch (e: Exception) {
                    resultText.text = "Invalid input. Please check your date format."
                }
            } else {
                resultText.text = "Please fill in all fields."
            }
        }
    }
}
