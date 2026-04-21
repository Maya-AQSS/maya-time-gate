package com.example.mayatimegate.data

import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.model.OdooRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
interface OdooApi{ // interface con metodo que busca al empleado por su rfid
    @GET("api/empleado_rfid/{rfid}") //Api para encontrar empleado por codigo rfid
    fun getEmployeeByRfid( //metodo que se conecta y devuelve la informacion del empleado
        @Path("rfid") rfid: String
    ): Call<EmployeeResponse>

    @GET("api/empleado_dni/{dni}") //Api para buscar por dni
    fun getEmployeeByDni(
        @Path("dni") dni: String
    ): Call<EmployeeResponse>

    @POST("api/v1/attendance/log") //Api para almacenar en odoo los logs de los fichajes
    suspend fun logAttendance(
        @Body request: OdooRequest
    )
}
