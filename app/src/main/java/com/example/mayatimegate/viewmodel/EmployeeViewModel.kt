package com.example.mayatimegate.viewmodel

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
                    if(serverResponse != null && serverResponse.status == "success"){
                        employeeInfo.value = serverResponse
                        errorMessage.value = null
                    }else{
                        //si hay algun error se le da valor al error
                        employeeInfo.value = null
                        errorMessage.value = serverResponse?.message ?: "Empleado no encontrado"

                    }

                }else{
                    errorMessage.value = "Error en el servidor: ${response.code()}"
                }
            }
            override fun onFailure(employee: Call<EmployeeResponse>, t: Throwable){
                employeeInfo.value = null
                errorMessage.value = "Fallo en red: ${t.message}"
            }
        })

    }

    fun searchByDni(stringDni: String){
        val dni = stringDni.toIntOrNull()

        if (dni != null){
            val call = repository.searchEmployeeByDni(dni)
        }

    }

    //Borra toda la informacion
    fun resetData(){
        employeeInfo.value= null
        errorMessage.value= null
    }
}