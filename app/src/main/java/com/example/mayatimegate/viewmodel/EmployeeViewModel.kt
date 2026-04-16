package com.example.mayatimegate.viewmodel

import androidx.lifecycle.*
import com.example.mayatimegate.data.EmployeeRepository
import com.example.mayatimegate.data.SettingsManager
import com.example.mayatimegate.model.EmployeeResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel( //Clase ViewModel para gestionar la logica de los empleados
    private val settingsManager: SettingsManager
) : ViewModel() {

    //Variables
    private val repository = EmployeeRepository()

    val employeeInfo = MutableLiveData<EmployeeResponse?>()
    val errorMessage = MutableLiveData<String?>()

    fun searchByRfid(rfid: String) { //Funcion para buscar empleado por RFID
        viewModelScope.launch {

            val currentUrl = settingsManager.formattedUrl.first()
            //Si la url es incorrecta no busca y se indica que hay un error
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,
                    null,
                    null,
                    null,
                    null,
                    message = "url-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                return@launch
            }
            //Llamada al repositorio para buscar al empleado
            val call = repository.searchEmployeeByRfid(rfid, currentUrl)

            call.enqueue(object : Callback<EmployeeResponse>{

                override fun onResponse( //Funcion que da valor al empleado
                    call: Call<EmployeeResponse>,
                    response: Response<EmployeeResponse>
                ) {
                    if (response.isSuccessful) {
                        val serverResponse = response.body()
                        employeeInfo.value = serverResponse

                        if (serverResponse != null && serverResponse.status == "success") {
                            errorMessage.value = null
                        } else {
                            errorMessage.value =
                                serverResponse?.message ?: "Empleado no encontrado"
                        }

                    } else {
                        errorMessage.value = "Error en el servidor: ${response.code()}"
                    }
                }
                //Funcion en caso de error de conexion
                override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                    employeeInfo.value = EmployeeResponse(
                        status = "error",
                        null,
                        null,
                        null,
                        null,
                        null,
                        message = "connection-error"
                    )
                    errorMessage.value = "Fallo en red: ${t.message}"
                }
            })
        }
    }

    fun searchByDni(stringDni: String) { //Funcion para buscar empleado por dni
        viewModelScope.launch {

            val currentUrl = settingsManager.formattedUrl.first()
            //En caso de que la url sea incorrecta, mostramos error
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,
                    null,
                    null,
                    null,
                    null,
                    message = "url-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                return@launch
            }
            val dni = stringDni.toLongOrNull()

            if (dni != null) { // si el dni tiene un tamaño aceptable
                val letter = calculateLetterOfDni(dni) //calculamos la letra del dni
                val officialDni = stringDni + letter //concatenamos las letras con el numero

                //buscamos al empleado por el dni
                val call = repository.searchEmployeeByDni(officialDni, currentUrl)

                call.enqueue(object : Callback<EmployeeResponse> {

                    override fun onResponse( //funcion que le da valor al empleado
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val serverResponse = response.body()
                            employeeInfo.value = serverResponse

                            if (serverResponse != null && serverResponse.status == "success") {
                                errorMessage.value = null
                            } else {
                                errorMessage.value =
                                    serverResponse?.message ?: "Empleado no encontrado"
                            }

                        } else {
                            errorMessage.value =
                                "Error en el servidor: ${response.code()}"
                        }
                    }
                    //Funcion que le da valor al empleado en caso de error
                    override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                        employeeInfo.value = EmployeeResponse(
                            status = "error",
                            null,
                            null,
                            null,
                            null,
                            null,
                            message = "connection-error"
                        )
                        errorMessage.value = "Fallo en red: ${t.message}"
                    }
                })

            } else { //si el dni es demasiado largo
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,
                    null,
                    null,
                    null,
                    null,
                    message = "DNI demasiado largo"
                )
            }
        }
    }

    fun resetData() { //Funcion que borra los datos volver a crear un empleado de nuevo
        employeeInfo.value = null
        errorMessage.value = null
    }

    fun calculateLetterOfDni(dni: Long): String { //funcion que calcula la letra del dni
        return when (dni % 23) {
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
    //funcion que comprueba si la url de odoo tiene un formato adecuado
    private fun isValidBaseUrl(url: String): Boolean {
        return try {
            url.toHttpUrl()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}

//Clase factory que crea una instancia de EmployeeViewModel
class EmployeeViewModelFactory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            //Si el view model es EmployeeViewModel devolvemos la instancia
            return EmployeeViewModel(settingsManager) as T
        }
        //Si hay algun error controlamos la excepcion
        throw IllegalArgumentException("Clase viewModel desconocida")
    }
}