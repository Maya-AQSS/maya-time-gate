package com.example.mayatimegate.model

import com.google.gson.annotations.SerializedName

data class EmployeeResponse(
    val status: String,
    @SerializedName("id_odoo")
    val odooId: Int?,
    @SerializedName("nombre")
    val name: String?,
    @SerializedName("apellidos")
    val surname: String?,
    val dni: String?,
    val rfid: String?,
    val imageUrl:String?,
    val message:String?,
    var isSigned: Boolean = false
){
    fun changeSignedState(){
        this.isSigned =  !isSigned
    }
}