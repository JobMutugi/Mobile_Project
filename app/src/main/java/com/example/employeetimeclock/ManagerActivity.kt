package com.example.employeetimeclock

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        setTitle("Manager Dashboard")

        val employeeListView = findViewById(R.id.employeeListView) as ListView
        val reportListView = findViewById(R.id.reportListView) as ListView
        val periodSpinner = findViewById(R.id.periodSpinner) as Spinner
        val presentText = findViewById(R.id.presentText) as TextView
        val absentText = findViewById(R.id.absentText) as TextView
        val refreshButton = findViewById(R.id.refreshButton) as Button

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        employeeListView.adapter = adapter

        val reportAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        reportListView.adapter = reportAdapter

        val periods = arrayOf("Today", "This Month")
        val periodAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        periodSpinner.adapter = periodAdapter

        refreshDashboard(adapter, reportAdapter, presentText, absentText, periodSpinner)

        refreshButton.setOnClickListener {
            refreshDashboard(adapter, reportAdapter, presentText, absentText, periodSpinner)
        }

        periodSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                refreshDashboard(adapter, reportAdapter, presentText, absentText, periodSpinner)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        employeeListView.onItemClickListener = android.widget.AdapterView.OnItemClickListener { _, _, position, _ ->
            val employee = DataManager.getInstance().getEmployees()[position]
            showShiftDialog(employee)
        }
    }

    private fun refreshDashboard(
        employeeAdapter: ArrayAdapter<String>,
        reportAdapter: ArrayAdapter<String>,
        presentText: TextView,
        absentText: TextView,
        periodSpinner: Spinner
    ) {
        val employees = DataManager.getInstance().getEmployees()
        val employeeNames = employees.map { "${it.name} (${it.role})" }
        employeeAdapter.clear()
        employeeAdapter.addAll(employeeNames)
        employeeAdapter.notifyDataSetChanged()

        val presentCount = DataManager.getInstance().getPresentCount()
        val absentCount = DataManager.getInstance().getAbsentCount()
        presentText.text = "Present: $presentCount"
        absentText.text = "Absent: $absentCount"

        val selectedPeriod = periodSpinner.selectedItem.toString()
        val records = when (selectedPeriod) {
            "Today" -> DataManager.getInstance().getTodayAttendance()
            else -> DataManager.getInstance().getAllAttendance()
        }

        val reportLines = records.map { record ->
            val hours = if (record.clockOut != null) String.format("%.2f", record.hoursWorked) else "N/A"
            "${record.employeeName} - ${record.date} - In: ${formatTime(record.clockIn)} - Out: ${formatTime(record.clockOut)} - Hours: $hours"
        }
        reportAdapter.clear()
        reportAdapter.addAll(reportLines)
        reportAdapter.notifyDataSetChanged()
    }

    private fun showShiftDialog(employee: Employee) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_shift, null)
        val startEdit = dialogView.findViewById(R.id.shiftStartEdit) as android.widget.EditText
        val endEdit = dialogView.findViewById(R.id.shiftEndEdit) as android.widget.EditText
        val saveButton = dialogView.findViewById(R.id.shiftSaveButton) as Button

        val existingShift = DataManager.getInstance().getShiftForEmployee(employee.id)
        startEdit.setText(existingShift?.startTime ?: employee.shiftStart)
        endEdit.setText(existingShift?.endTime ?: employee.shiftEnd)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Set Shift for ${employee.name}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val start = startEdit.text.toString().trim()
                val end = endEdit.text.toString().trim()
                if (start.isNotEmpty() && end.isNotEmpty()) {
                    DataManager.getInstance().addShift(Shift(employee.id, start, end))
                    Toast.makeText(this, "Shift saved for ${employee.name}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
        dialog.show()
    }

    private fun formatTime(date: Date?): String {
        if (date == null) return "--:--"
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
}
