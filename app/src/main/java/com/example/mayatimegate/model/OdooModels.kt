package com.example.mayatimegate.model

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

