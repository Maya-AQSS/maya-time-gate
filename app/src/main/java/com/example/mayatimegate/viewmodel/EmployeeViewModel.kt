package com.example.mayatimegate.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mayatimegate.data.EmployeeRepository
import com.example.mayatimegate.model.EmployeeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel: ViewModel(){
    private val repository = EmployeeRepository() //Inicializacion del repositorio

    val employeeInfo = MutableLiveData<EmployeeResponse?>() // inicializacion del empleado
    val errorMessage = MutableLiveData<String?>() //inicializacion por si hay algun error

    fun searchByRfid(rfid: String){
        val call = repository.searchEmployeeByRfid(rfid)

        call.enqueue(object : Callback<EmployeeResponse>{
            override fun onResponse(employee: Call<EmployeeResponse>, response: Response<EmployeeResponse>){
                //si se encuentra al empleado en odoo se guarda su informaicon en el objeto employeeInfo
                if (response.isSuccessful){
                    val serverResponse = response.body()
                    //Si va bien se le da valor al empleado
                    employeeInfo.value = serverResponse

                    if(serverResponse != null && serverResponse.status == "success"){
                        errorMessage.value = null
                    }else{
                        //si hay algun error se le da valor al error
                        errorMessage.value = serverResponse?.message ?: "Empleado no encontrado"

                    }

                }else{
                    errorMessage.value = "Error en el servidor: ${response.code()}"
                }
            }
            override fun onFailure(employee: Call<EmployeeResponse>, t: Throwable){
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,
                    null,
                    null,
                    null,
                    null,
                    message = "connection-error")
                errorMessage.value = "Fallo en red: ${t.message}"
            }
        })

    }

    fun searchByDni(stringDni: String){
        val dni = stringDni.toLongOrNull()
        if (dni != null){
            val letter = calculateLetterOfDni(dni)
            val officialDni = stringDni + letter
            val call = repository.searchEmployeeByDni(officialDni)
            call.enqueue(object : Callback<EmployeeResponse>{
                override fun onResponse(employee: Call<EmployeeResponse>, response: Response<EmployeeResponse>){
                    //si se encuentra al empleado en odoo se guarda su informaicon en el objeto employeeInfo
                    if (response.isSuccessful){
                        val serverResponse = response.body()
                        //Si va bien se le da valor al empleado
                        employeeInfo.value = serverResponse
                        if(serverResponse != null && serverResponse.status == "success"){
                            errorMessage.value = null
                        }else{
                            //si hay algun error se le da valor al error
                            errorMessage.value = serverResponse?.message ?: "Empleado no encontrado"
                        }

                    }else{
                        errorMessage.value = "Error en el servidor: ${response.code()}"
                    }
                }
                override fun onFailure(employee: Call<EmployeeResponse>, t: Throwable){
                    employeeInfo.value = EmployeeResponse(
                        status = "error",
                        null,
                        null,
                        null,
                        null,
                        null,
                        message = "connection-error")
                    errorMessage.value = "Fallo en red: ${t.message}"
                }
            })
        }else{
            employeeInfo.value = EmployeeResponse(
                status = "error",
                null,
                null,
                null,
                null,
                null,
                message = "DNI demasiado largo")
        }

    }

    //Borra toda la informacion
    fun resetData(){
        employeeInfo.value= null
        errorMessage.value= null
    }


    //Funcion para calcular la letra del dni en funcion de los numeros introducidos
    fun calculateLetterOfDni(dni:Long): String{
        return when (dni % 23){
            0L -> "T"
            1L -> "R"
            2L -> "W"
            3L -> "A"
            4L -> "G"
            5L -> "M"
            6L -> "Y"
            7L -> "F"
            8L -> "P"
            9L -> "D"
            10L -> "X"
            11L -> "B"
            12L -> "N"
            13L -> "J"
            14L -> "Z"
            else -> "S"
        }
    }
}