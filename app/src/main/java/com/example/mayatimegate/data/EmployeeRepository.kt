package com.example.mayatimegate.data

import com.example.mayatimegate.model.CheckDoubleSigning
import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.model.LastSession
import com.example.mayatimegate.model.LastSigning
import retrofit2.Call

class EmployeeRepository {//Repositorio que implementa las demas clases para realizar la busqueda
    //funciones que devuelven la informacion del empleado

    fun searchEmployeeByRfid(rfid: String, apiKey: String, baseUrl: String): Call<EmployeeResponse> {
        // Obtenemos la instancia dinámica usando la URL que nos pasan
        val api = RetrofitClient.getOdooApi(baseUrl)
        return api.getEmployeeByRfid(rfid, apiKey)
    }

    fun searchEmployeeByDni(dni: String, apiKey: String, baseUrl: String): Call<EmployeeResponse> {
        val api = RetrofitClient.getOdooApi(baseUrl)
        return api.getEmployeeByDni(dni, apiKey)
    }

    fun checkDoubleSigning(id: Int, apiKey: String, baseUrl: String): Call<CheckDoubleSigning>{
        val api = RetrofitClient.getOdooApi(baseUrl)
        return api.checkDoubleSigning(id, apiKey)
    }

    fun searchLastSession(apiKey: String, baseUrl:String): Call<LastSession>{
        val api = RetrofitClient.getOdooApi(baseUrl)
        return api.getLastSession(apiKey)
    }

    fun searchLastSigning(id: Int, apiKey: String, baseUrl: String): Call<LastSigning> {
        val api = RetrofitClient.getOdooApi(baseUrl)
        return api.getLastSigning(id, apiKey)
    }
}