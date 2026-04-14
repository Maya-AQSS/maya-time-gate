package com.example.mayatimegate.data

import com.example.mayatimegate.model.EmployeeResponse
import retrofit2.Call

class EmployeeRepository { //Repositorio que implementa las demas clases para realizar la busqueda
    private val api = RetrofitClient.odooApi

    //funcion que devuelve la informacion del empleado
    fun searchEmployeeByRfid(rfid: String): Call<EmployeeResponse>{ //busca por rfid
        return api.getEmployeeByRfid(rfid)
    }

    fun searchEmployeeByDni(dni: Int): Call<EmployeeResponse>{ //busca por dni
        return api.getEmployeeByDni(dni)
    }
}