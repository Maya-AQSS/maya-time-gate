package com.example.mayatimegate.model

import java.time.LocalDate
import java.time.LocalTime

data class Employee(
    val employeeId: Int,
    val surname: String,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val type: Char,
    val rfid: Int,
    val locationId: Int,
)

data class EmployeeResponse(
    val results: List<Employee>
)