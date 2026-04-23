package com.example.mayatimegate.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import com.example.mayatimegate.data.EmployeeRepository
import com.example.mayatimegate.data.RetrofitClient
import com.example.mayatimegate.data.SettingsManager
import com.example.mayatimegate.model.AttendanceParams
import com.example.mayatimegate.model.CheckDoubleSigning
import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.model.OdooRequest
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

    var doubleSignInfo = MutableLiveData<CheckDoubleSigning?>()
    val errorMessage = MutableLiveData<String?>()

    //HashMap para guardar la informacion de los empleados dependiendo de su identificador de fihcajes
    private val hashMapRfid: HashMap<String, EmployeeResponse> = hashMapOf()
    private val hashMapDni: HashMap<String, EmployeeResponse> = hashMapOf()

    fun searchByRfid(rfid: String) { //Funcion para buscar empleado por RFID
        viewModelScope.launch {

            val currentUrl = settingsManager.formattedUrl.first()
            //Si la url es incorrecta no busca y se indica que hay un error
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,null,null,null,null,null,
                    message = "connection-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                return@launch
            }

            val employee = getEmployeeFromHashMap(rfid) //se busca el empleado en el HashMap

            if(employee == null){ //Si no se encuentra el empleado en memoria
                //Llamada al repositorio para buscar al empleado
                val call = repository.searchEmployeeByRfid(rfid, currentUrl)

                call.enqueue(object : Callback<EmployeeResponse>{

                    override fun onResponse( //Funcion que da valor al empleado
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val serverResponse = response.body()
                            employeeInfo.value = serverResponse //Se le da valor al objeto
                            employeeInfo.value?.rfid = rfid  //Como el rfid no esta en el json de la api se le da valor
                            if (serverResponse != null && serverResponse.status == "success") {
                                saveEmployee(employeeInfo.value) //se guarda el empleado en memoria
                                employeeInfo.value?.changeSignedState()

                                val employeeId = employeeInfo.value?.odooId
                                val employeeState = employeeInfo.value?.isSigned
                                checkDoubleSigning(employeeId!!) { result ->

                                    if (result?.status == "success") {

                                        logSigning(employeeId, employeeState)
                                        errorMessage.value = null
                                        doubleSignInfo.value = result
                                    }
                                }
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
                            null,null,null,null,null,null,
                            message = "connection-error"
                        )
                        errorMessage.value = "Fallo en red: ${t.message}"
                    }
                })
            }else{ //si el empleado ya existe en memoria
                employee.changeSignedState()  //se le cambia el estado de fichaje
                employeeInfo.value = employee //se le da valor al objeto empleado
                val employeeId = employeeInfo.value?.odooId
                val employeeState = employeeInfo.value?.isSigned
                checkDoubleSigning(employeeId!!) { result ->
                    if (result?.status == "success") {
                        logSigning(employeeId, employeeState)
                        errorMessage.value = null
                        doubleSignInfo.value = result
                    }
                }
            }
        }
    }

    fun searchByDni(stringDni: String) { //Funcion para buscar empleado por dni

        viewModelScope.launch {

            val currentUrl = settingsManager.formattedUrl.first()
            //En caso de que la url sea incorrecta, mostramos error
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,null,null,null,null,null,
                    message = "connection-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                return@launch
            }
            val dni = stringDni.toLongOrNull()

            if (dni != null) { // si el dni tiene un tamaño aceptable
                val letter = calculateLetterOfDni(dni) //calculamos la letra del dni
                val officialDni = stringDni + letter //concatenamos las letras con el numero

                //Buscamos si tenemos guardado el empleado en el hashmap
                val employee = getEmployeeFromHashMap(officialDni)
                if(employee == null){
                    //Si no lo tenemos
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

                                    saveEmployee(employeeInfo.value)
                                    employeeInfo.value?.changeSignedState()

                                    val employeeId = employeeInfo.value?.odooId
                                    val employeeState = employeeInfo.value?.isSigned
                                    checkDoubleSigning(employeeId!!) { result ->

                                        if (result?.status == "success") {

                                            logSigning(employeeId, employeeState)
                                            errorMessage.value = null
                                            doubleSignInfo.value = result
                                        }
                                    }
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
                                null,null,null,null,null,null,
                                message = "connection-error"
                            )
                            errorMessage.value = "Fallo en red: ${t.message}"
                        }
                    })
                }else{
                    employee.changeSignedState()
                    employeeInfo.value = employee
                    val employeeId = employeeInfo.value?.odooId
                    val employeeState = employeeInfo.value?.isSigned
                    checkDoubleSigning(employeeId!!) { result ->
                        if (result?.status == "success") {
                            logSigning(employeeId, employeeState)
                            errorMessage.value = null
                            doubleSignInfo.value = result
                        }
                    }
                }


            } else { //si el dni es demasiado largo
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,
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

    fun checkDoubleSigning(id: Int, onResult: (CheckDoubleSigning?) -> Unit) {

        viewModelScope.launch {

            val currentUrl = settingsManager.formattedUrl.first()

            if (!isValidBaseUrl(currentUrl)) {
                errorMessage.value = "URL inválida"
                onResult(null)
                return@launch
            }

            repository.checkDoubleSigning(id, currentUrl)
                .enqueue(object : Callback<CheckDoubleSigning> {

                    override fun onResponse(
                        call: Call<CheckDoubleSigning>,
                        response: Response<CheckDoubleSigning>
                    ) {
                        onResult(response.body())
                    }

                    override fun onFailure(call: Call<CheckDoubleSigning>, t: Throwable) {
                        errorMessage.value = "Error de red: ${t.message}"
                        onResult(null)
                    }
                })
        }
    }
    //Funcion para guardar en un hashmap la informacion de los empleados
    fun saveEmployee(employee: EmployeeResponse?){
        val rfid = employee?.rfid
        val dni = employee?.dni


        if(rfid != null){
            hashMapRfid.put(rfid, employee)
        }
        if(dni != null){
            hashMapDni.put(dni, employee)
        }

    }
    //Funcion para encontrar la informacion de empleado en el hashmap
    fun getEmployeeFromHashMap(key: String): EmployeeResponse?{
        return hashMapRfid[key] ?: hashMapDni[key]
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

    //Funcion para enviar datos a la api que registrara los fichajes de los empleados
    fun logSigning(employeeId: Int?, state: Boolean?) {
        viewModelScope.launch {
            val currentUrl = settingsManager.formattedUrl.first()

            //Comprueba que el id y el estado del empleado no sean nulos
            if (employeeId == null || state == null) {
                return@launch
            }
            //Consigue el nombre del dispositivo y el estado del empleado
            val deviceName = settingsManager.deviceName.first()
            val apiState = if(state) "I" else "O"

            val request = OdooRequest( // Crea el mensaje que le mandara a la api
                params = AttendanceParams(
                    employee_id =employeeId,
                    type = apiState,
                    terminal_id =deviceName,
                    location_id = 1
                )
            )

            try {
                //Llamada a la api para enviarle los datos del empleado
                RetrofitClient.getOdooApi(currentUrl).logAttendance(request)
            } catch (e: Exception) {
                println("Error al guardar los datos en attendance: ${e.message}")

            }
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