package com.example.employeetimeclock

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var employeeId: String
    private lateinit var employeeName: String
    private lateinit var role: String

    private lateinit var clockInButton: Button
    private lateinit var clockOutButton: Button
    private lateinit var statusText: TextView
    private lateinit var hoursText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        employeeId = intent.getStringExtra("employeeId") ?: ""
        employeeName = intent.getStringExtra("employeeName") ?: "Employee"
        role = intent.getStringExtra("role") ?: "employee"

        setTitle("Employee Time Clock")

        statusText = findViewById(R.id.statusText)
        hoursText = findViewById(R.id.hoursText)
        clockInButton = findViewById(R.id.clockInButton)
        clockOutButton = findViewById(R.id.clockOutButton)

        val employee = DataManager.getInstance().getEmployee(employeeId)
        if (employee != null) {
            statusText.text = "Welcome, ${employee.name}"
        }

        updateUI()

        clockInButton.setOnClickListener {
            val record = DataManager.getInstance().clockIn(employeeId, employeeName)
            updateUI()
            Toast.makeText(this, "Clocked in at ${formatTime(record.clockIn)}", Toast.LENGTH_SHORT).show()
        }

        clockOutButton.setOnClickListener {
            val record = DataManager.getInstance().clockOut(employeeId)
            if (record != null) {
                updateUI()
                Toast.makeText(this, "Clocked out at ${formatTime(record.clockOut)}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "You are not clocked in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val isClockedIn = DataManager.getInstance().isClockedIn(employeeId)
        clockInButton.isEnabled = !isClockedIn
        clockOutButton.isEnabled = isClockedIn

        if (isClockedIn) {
            statusText.text = "$employeeName is currently clocked in"
        } else {
            statusText.text = "$employeeName is not clocked in"
        }

        val todayRecords = DataManager.getInstance().getAttendance(employeeId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayRecord = todayRecords.find { it.date == today && it.clockIn != null }
        val hours = if (todayRecord != null) todayRecord.hoursWorked else 0.0
        hoursText.text = "Hours worked today: ${String.format("%.2f", hours)} hours"
    }

    private fun formatTime(date: Date?): String {
        if (date == null) return "--:--"
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}
