package com.example.mayatimegate.data

import com.example.mayatimegate.model.Employee
import retrofit2.Call

class EmployeeRepository {
    private val api = RetrofitClient.odooApi

    fun searchEmployee(rfid: String): Call<Employee>{
        return api.getEmployeeByRfid(rfid)
    }
}