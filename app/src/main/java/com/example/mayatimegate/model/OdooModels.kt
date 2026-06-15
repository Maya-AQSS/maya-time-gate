package com.example.mayatimegate.model

import com.google.gson.annotations.SerializedName
import java.sql.Time

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


data class CheckDoubleSigning( // respuesta de la api para comprobar si el fichaje es doble
    val status: String,
    @SerializedName("id_odoo")
    val employee_id: Int,
    @SerializedName("fichaje_doble")
    val doubleSigning: Boolean
)

data class LastSession( // respuesta de la api para comprobar si llega tarde a la ultima sesion
    val status: String,
    @SerializedName("fichaje_tarde")
    val isLate :Boolean,
)


data class LastSigning(
    val status: String,
    @SerializedName("tipo_fichaje")
    val type: Boolean
)