package com.example.mayatimegate.model

import com.google.gson.annotations.SerializedName

// Lo que enviamos (Request)
data class OdooRequest( //Estructura del json
    val jsonrpc: String = "2.0",
    val params: AttendanceParams
)

data class AttendanceParams( //contenido del empleado en el json
    val employee_id: Int,
    val type: String,
    val terminal_id: String,
    val location_id: Int
)

data class CheckDoubleSigning(
    val status: String,
    @SerializedName("id_odoo")
    val employee_id: Int,
    @SerializedName("fichaje_doble")
    val doubleSigning: Boolean
)

