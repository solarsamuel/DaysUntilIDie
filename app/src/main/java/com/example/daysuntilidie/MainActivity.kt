package com.example.daysuntilidie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    private lateinit var monthInput: EditText
    private lateinit var dayInput: EditText
    private lateinit var yearInput: EditText
    private lateinit var expectedAgeInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultText: TextView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "DaysUntilIDiePrefs"
        const val PREF_MONTH = "pref_month"
        const val PREF_DAY = "pref_day"
        const val PREF_YEAR = "pref_year"
        const val PREF_EXPECTED_AGE = "pref_expected_age"
    }

    private var screenReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        monthInput = findViewById(R.id.monthInput)
        dayInput = findViewById(R.id.dayInput)
        yearInput = findViewById(R.id.yearInput)
        expectedAgeInput = findViewById(R.id.expectedAgeInput)
        calculateButton = findViewById(R.id.calculateButton)
        resultText = findViewById(R.id.resultText)

        // Load saved data if available
        loadSavedData()

        calculateButton.setOnClickListener {
            try {
                // Parse and validate inputs
                val month = monthInput.text.toString().toInt()
                val day = dayInput.text.toString().toInt()
                val year = yearInput.text.toString().toInt()
                val expectedAge = expectedAgeInput.text.toString().toInt()

                validateInputs(month, day, year)

                // Save inputs to SharedPreferences
                saveInputToPreferences(month, day, year, expectedAge)

                // Calculate days left
                calculateDaysLeft()

            } catch (e: Exception) {
                resultText.text = e.message
            }
        }

        // Register screen ON receiver to update the widget
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_SCREEN_ON) {
                    calculateDaysLeft()
                }
            }
        }.also { registerReceiver(it, IntentFilter(Intent.ACTION_SCREEN_ON)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        screenReceiver?.let { unregisterReceiver(it) }
    }

    fun loadSavedData() {
        val month = sharedPreferences.getInt(PREF_MONTH, -1)
        val day = sharedPreferences.getInt(PREF_DAY, -1)
        val year = sharedPreferences.getInt(PREF_YEAR, -1)
        val expectedAge = sharedPreferences.getInt(PREF_EXPECTED_AGE, -1)

        if (month != -1 && day != -1 && year != -1 && expectedAge != -1) {
            monthInput.setText(month.toString())
            dayInput.setText(day.toString())
            yearInput.setText(year.toString())
            expectedAgeInput.setText(expectedAge.toString())
            calculateDaysLeft()
        }
    }

    private fun saveInputToPreferences(month: Int, day: Int, year: Int, expectedAge: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(PREF_MONTH, month)
        editor.putInt(PREF_DAY, day)
        editor.putInt(PREF_YEAR, year)
        editor.putInt(PREF_EXPECTED_AGE, expectedAge)
        editor.apply()
    }

    private fun validateInputs(month: Int, day: Int, year: Int) {
        if (month !in 1..12) throw IllegalArgumentException("Invalid month. Please enter a value between 1 and 12.")
        if (day !in 1..31) throw IllegalArgumentException("Invalid day. Please enter a valid day.")
        if (year !in 1900..2023) throw IllegalArgumentException("Invalid year. Please enter a valid year.")
    }

    fun calculateDaysLeft() {
        val month = sharedPreferences.getInt(PREF_MONTH, -1)
        val day = sharedPreferences.getInt(PREF_DAY, -1)
        val year = sharedPreferences.getInt(PREF_YEAR, -1)
        val expectedAge = sharedPreferences.getInt(PREF_EXPECTED_AGE, -1)

        if (month != -1 && day != -1 && year != -1 && expectedAge != -1) {
            val birthdate = LocalDate.of(year, month, day)
            val deathDate = birthdate.plusYears(expectedAge.toLong())
            val today = LocalDate.now()
            val daysLeft = ChronoUnit.DAYS.between(today, deathDate)

            val result = if (daysLeft >= 0) {
                "Days left: $daysLeft"
            } else {
                "You're already ${-daysLeft / 365} years past your expected lifespan!"
            }

            resultText.text = result

            // Update the widget
            //DaysWidgetProvider.updateAllWidgets(this)
            DaysWidgetProvider.updateAllWidgets(this)
        }
    }
}
