package com.example.mayatimegate.data

import com.example.mayatimegate.model.Employee
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
interface OdooApi{
    @GET("api/empleado/{rfid}")
    fun getEmployeeByRfid(
        @Path("rfid") rfid: String
    ): Call<Employee>
}