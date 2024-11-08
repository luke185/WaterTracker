package com.example.watertracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.watertracker.containers.Bottle
import com.example.watertracker.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // Initialise Binding
    private lateinit var binding: ActivityMainBinding

    // Initialise SharedPreferences
    private lateinit var sharedPref: SharedPreferences

    // Initialise variables
    private var bottle50 = Bottle("bottle", 50)
    private var bottle100 = Bottle("bottle", 100)
    private var bottle250 = Bottle("bottle", 250)
    private var bottle500 = Bottle("bottle", 500)
    private var bottle600 = Bottle("bottle", 600)
    private var bottle1000 = Bottle("bottle", 1000)
    private var waterGoal: Double = 4000.00
    private var waterTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise buttons
        binding.addWaterButton.setOnClickListener { addWater(binding.waterInput.text.toString()) }
        binding.updateGoalButton.setOnClickListener { updateGoal() }
        binding.addButton50.setOnClickListener { addWater(bottle50.volume.toString()) }
        binding.addButton100.setOnClickListener { addWater(bottle100.volume.toString()) }
        binding.addButton250.setOnClickListener { addWater(bottle250.volume.toString()) }
        binding.addButton500.setOnClickListener { addWater(bottle500.volume.toString()) }
        binding.addButton600.setOnClickListener { addWater(bottle600.volume.toString()) }
        binding.addButton1000.setOnClickListener { addWater(bottle1000.volume.toString()) }
        binding.resetButton.setOnClickListener { reset() }

        // Load shared preferences
        sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE) ?: return

    }

    /**
     * Override onResume to refresh all values
     */
    override fun onResume() {
        super.onResume()
        loadPrefs()
        updateValues()
    }

    /**
     * Update the current water goal
     */
    private fun updateGoal() {
        // Get the new value from user input
        val newGoal = binding.waterInput.text.toString().toDoubleOrNull() ?: return
        // Update actual value of waterGoal
        waterGoal = newGoal
        // Update values
        updateValues()
    }

    /**
     * Reset water total to zero, then update all values
     */
    private fun reset() {
        waterTotal = 0.00
        updateValues()
    }

    /**
     * Add water from user input or bottle, to water total
     */
    private fun addWater(source: String) {
        // Get user input as double, pass if empty
        val waterSourceAmount: Double = source.toDoubleOrNull() ?: return
        // Add source to current water total, then update values
        waterTotal += waterSourceAmount
        updateValues()
    }

    /**
     * Update all values on screen, then save them
     */
    private fun updateValues() {
        // Calculate new water percentage
        val newPercentage: Double = (waterTotal / waterGoal) * 100
        // Update percentage bar
        binding.waterPercentage.text = newPercentage.roundToInt().toString()
        binding.progressBar.progress = newPercentage.roundToInt()
        // Clear input field
        binding.waterInput.setText("")
        // Update tracker
        val trackerTextString =
            "$waterTotal mL / $waterGoal mL\n${waterGoal - waterTotal} mL left"
        binding.trackerText.text = trackerTextString
        // Save new values
        savePrefs()
    }

    /**
     * Save water Total + Goal
     */
    private fun savePrefs() {
        with(sharedPref.edit()) {
            putFloat("waterTotal", waterTotal.toFloat())
            putFloat("waterGoal", waterGoal.toFloat())
            apply()
        }
    }

    /**
     * Load water Total + Goal
     */
    private fun loadPrefs() {
        waterTotal = sharedPref.getFloat("waterTotal", 0.00F).toDouble()
        waterGoal = sharedPref.getFloat("waterGoal", 4000.00F).toDouble()
    }

}