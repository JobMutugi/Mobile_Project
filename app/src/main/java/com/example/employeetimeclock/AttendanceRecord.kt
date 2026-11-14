package com.example.employeetimeclock

import java.util.Date

data class AttendanceRecord(
    val id: String,
    val employeeId: String,
    val employeeName: String,
    val date: String,
    val clockIn: Date?,
    val clockOut: Date?,
    val hoursWorked: Double = 0.0
)
