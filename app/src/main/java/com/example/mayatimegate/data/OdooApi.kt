package com.example.mayatimegate.data

import com.example.mayatimegate.model.EmployeeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
interface OdooApi{ // interface con metodo que busca al empleado por su rfid
    @GET("api/empleado/{rfid}")
    fun getEmployeeByRfid( //metodo que se conecta y devuelve la informacion del empleado
        @Path("rfid") rfid: String
    ): Call<EmployeeResponse>
}