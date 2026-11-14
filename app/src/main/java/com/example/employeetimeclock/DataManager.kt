package com.example.employeetimeclock

import java.util.Date

class DataManager {
    companion object {
        @Volatile
        private var instance: DataManager? = null

        fun getInstance(): DataManager {
            return instance ?: synchronized(this) {
                instance ?: DataManager().also { instance = it }
            }
        }
    }

    private val employees = mutableListOf<Employee>()
    private val attendance = mutableListOf<AttendanceRecord>()
    private val shifts = mutableListOf<Shift>()

    fun addEmployee(employee: Employee) {
        employees.add(employee)
    }

    fun getEmployees(): List<Employee> {
        return employees.toList()
    }

    fun getEmployee(id: String): Employee? {
        return employees.find { it.id == id }
    }

    fun clockIn(employeeId: String, employeeName: String): AttendanceRecord {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val recordId = employeeId + "_" + date
        val existing = attendance.find { it.id == recordId }
        if (existing != null && existing.clockOut == null) {
            return existing
        }
        val record = AttendanceRecord(
            id = recordId,
            employeeId = employeeId,
            employeeName = employeeName,
            date = date,
            clockIn = Date(),
            clockOut = null,
            hoursWorked = 0.0
        )
        attendance.add(record)
        return record
    }

    fun clockOut(employeeId: String): AttendanceRecord? {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val recordId = employeeId + "_" + date
        val record = attendance.find { it.id == recordId && it.clockOut == null }
        if (record != null) {
            val clockOutTime = Date()
            val clockInTime = record.clockIn ?: Date()
            val hours = (clockOutTime.time - clockInTime.time) / (1000.0 * 60.0 * 60.0)
            val updated = record.copy(
                clockOut = clockOutTime,
                hoursWorked = hours
            )
            val index = attendance.indexOfFirst { it.id == recordId }
            if (index >= 0) {
                attendance[index] = updated
            }
            return updated
        }
        return null
    }

    fun getAttendance(employeeId: String): List<AttendanceRecord> {
        return attendance.filter { it.employeeId == employeeId }
    }

    fun getAllAttendance(): List<AttendanceRecord> {
        return attendance.toList()
    }

    fun getTodayAttendance(): List<AttendanceRecord> {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        return attendance.filter { it.date == today }
    }

    fun isClockedIn(employeeId: String): Boolean {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val recordId = employeeId + "_" + date
        return attendance.any { it.id == recordId && it.clockOut == null }
    }

    fun addShift(shift: Shift) {
        shifts.add(shift)
    }

    fun getShifts(): List<Shift> {
        return shifts.toList()
    }

    fun getShiftForEmployee(employeeId: String): Shift? {
        return shifts.find { it.employeeId == employeeId }
    }

    fun getPresentCount(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val clockedIn = attendance.filter { it.date == today && it.clockOut == null }.map { it.employeeId }.toSet()
        return employees.count { clockedIn.contains(it.id) }
    }

    fun getAbsentCount(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val clockedIn = attendance.filter { it.date == today && it.clockOut == null }.map { it.employeeId }.toSet()
        return employees.count { !clockedIn.contains(it.id) && it.role == "employee" }
    }

    fun seedDemoData() {
        if (employees.isEmpty()) {
            addEmployee(Employee("1", "John Doe", "john@company.com", "employee", "09:00", "17:00"))
            addEmployee(Employee("2", "Jane Smith", "jane@company.com", "employee", "09:00", "17:00"))
            addEmployee(Employee("3", "Mike Wilson", "mike@company.com", "manager", "09:00", "17:00"))
            addEmployee(Employee("4", "Sarah Brown", "sarah@company.com", "employee", "08:30", "16:30"))
            addShift(Shift("1", "09:00", "17:00"))
            addShift(Shift("2", "09:00", "17:00"))
            addShift(Shift("4", "08:30", "16:30"))
        }
    }
}
