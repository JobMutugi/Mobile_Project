package com.example.employeetimeclock

data class Employee(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val shiftStart: String = "09:00",
    val shiftEnd: String = "17:00"
)
