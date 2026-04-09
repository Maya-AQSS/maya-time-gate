package com.example.mayatimegate.data

import com.example.mayatimegate.model.EmployeeResponse
import retrofit2.Call

class EmployeeRepository { //Repositorio que implementa las demas clases para realizar la busqueda
    private val api = RetrofitClient.odooApi

    fun searchEmployee(rfid: String): Call<EmployeeResponse>{ //funcion que devuelve la informacion del empleado
        return api.getEmployeeByRfid(rfid)
    }
}