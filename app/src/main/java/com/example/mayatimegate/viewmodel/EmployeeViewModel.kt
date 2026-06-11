package com.example.mayatimegate.viewmodel

import android.util.Log
import androidx.collection.doubleListOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.example.mayatimegate.data.EmployeeRepository
import com.example.mayatimegate.data.RetrofitClient
import com.example.mayatimegate.data.SettingsManager
import com.example.mayatimegate.model.AttendanceParams
import com.example.mayatimegate.model.CheckDoubleSigning
import com.example.mayatimegate.model.EmployeeResponse
import com.example.mayatimegate.model.LastSession
import com.example.mayatimegate.model.LastSigning
import com.example.mayatimegate.model.OdooRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    val lastSessionInfo = MutableLiveData<LastSession?>()
    val errorMessage = MutableLiveData<String?>()


    //HashMap para guardar la informacion de los empleados dependiendo de su identificador de fihcajes
    private val hashMapRfid: HashMap<String, EmployeeResponse> = hashMapOf()
    private val hashMapDni: HashMap<String, EmployeeResponse> = hashMapOf()

    private val _confirmSigning = MutableSharedFlow<EmployeeResponse>()
    var latestSuccessfulSigning: Boolean? = null



    fun searchByRfid(rfid: String) { //Funcion para buscar empleado por RFID
        employeeInfo.value = EmployeeResponse( //establece un empleado por defecto con un estado de carga
            status = "loading",
            null,null,null,null,null,null,
            message = null
        )

        viewModelScope.launch {
            val apiKey = settingsManager.apiKey.first()
            val currentUrl = settingsManager.formattedUrl.first()
            //Si la url es incorrecta no busca y se indica que hay un error
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null,null,null,null,null,null,
                    message = "connection-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                Log.d("Empleado", "ERROR: ${errorMessage.value}")
                return@launch
            }

            val cachedEmployee = getEmployeeFromHashMap(rfid) //se busca el empleado en el HashMap

            if(cachedEmployee != null) { //Si no se encuentra el empleado en memoria
                handleEmployeeFlow(cachedEmployee, apiKey, currentUrl)
            }else{

                //Llamada al repositorio para buscar al empleado
                val call = repository.searchEmployeeByRfid(rfid, apiKey,currentUrl)

                call.enqueue(object : Callback<EmployeeResponse>{

                    override fun onResponse( //Funcion que da valor al empleado
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (!response.isSuccessful) { //si la respuesta es erronea se guarda el error
                            errorMessage.value = "Error en el servidor: ${response.code()}"
                            Log.d("Empleado", "ERROR: ${errorMessage.value} (85)")
                            return
                        }

                        val serverResponse = response.body() //se guarda la respuesta

                        //si la respuesta es nula o no es exitosa se da error
                        if(serverResponse ==null || serverResponse.status != "success"){
                            errorMessage.value = serverResponse?.message ?: "Empleado no encontrado"
                            employeeInfo.value = EmployeeResponse(
                                status = "error", // se lanza error
                                null, null, null, null, null, null,
                                message = "error"
                            )
                            Log.d("Empleado", "ERROR: ${errorMessage.value} (99)")
                            return
                        }

                        serverResponse.rfid = rfid//se guarda el codigo rfid
                        saveEmployee(serverResponse) //se guarda el empleado
                        handleEmployeeFlow(serverResponse, apiKey, currentUrl) //se procede al flujo de fichaje


                    }
                    //Funcion en caso de error de conexion
                    override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                        employeeInfo.value = EmployeeResponse(
                            status = "error",
                            null,null,null,null,null,null,
                            message = "connection-error"
                        )
                        errorMessage.value = "Fallo en red: ${t.message}"
                        Log.d("Empleado", "ERROR: ${errorMessage.value} (118)")
                    }
                })
            }
        }
    }

    fun searchByDni(inputDni: String) { //funcion para buscar empleado por dni
        employeeInfo.value = EmployeeResponse( //establece un empleado por defecto con un estado de carga
            status = "loading",
            null,null,null,null,null,null,
            message = null
        )
        viewModelScope.launch {
            val apiKey = settingsManager.apiKey.first()
            val currentUrl = settingsManager.formattedUrl.first() //se consige la URL

            // se valida la URL
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse( //si la URL no cumple con el formato se da error
                    status = "error",
                    null, null, null, null, null, null,
                    message = "connection-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
                Log.d("Empleado", "ERROR: ${errorMessage.value} (143)")
                return@launch
            }

            // se comprueba y se calcula la letra del dni
            val dniNumber = inputDni.toLongOrNull()
            if (dniNumber == null) { // si hay algun error da error
                employeeInfo.value = EmployeeResponse(
                    status = "error",
                    null, null, null, null, null, null,
                    message = "DNI inválido"
                )
                Log.d("Empleado", "ERROR: ${errorMessage.value} (155)")
                return@launch
            }
            val officialDni = inputDni + calculateLetterOfDni(dniNumber)

            //se busca si el empleado exite en memoria
            val cachedEmployee = getEmployeeFromHashMap(officialDni)
            Log.d("Empleado", "DEBUG: ${apiKey}")
            if (cachedEmployee != null) { //si no es nulo es que existe en memoria
                handleEmployeeFlow(cachedEmployee,apiKey, currentUrl) // y se procesa al empleado para fichar
            } else { //si no esta en memoria se busca en la api
                val call = repository.searchEmployeeByDni(officialDni, apiKey, currentUrl) //se devuelve informacionde la api

                call.enqueue(object : Callback<EmployeeResponse> {

                    override fun onResponse( //si la llamada a la api ha encontrado informacion
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (!response.isSuccessful) { // si el estado no es satisfactorio
                            errorMessage.value = "Error en el servidor: ${response.code()}" //se da error
                            Log.d("Empleado", "ERROR: ${errorMessage.value} (176)")
                            return
                        }
                        val serverResponse = response.body() // si va bien se captura la respuesta

                        // si la respuesta da error
                        if (serverResponse == null || serverResponse.status != "success") {
                            errorMessage.value = serverResponse?.message ?: "Empleado no encontrado"
                            employeeInfo.value = EmployeeResponse(
                                status = "error", // se lanza error
                                null, null, null, null, null, null,
                                message = "error"
                            )
                            Log.d("Empleado", "ERROR: ${errorMessage.value} (189)")
                            return
                        }
                        saveEmployee(serverResponse) // se guarda el empleado en memoria
                        handleEmployeeFlow(serverResponse, apiKey, currentUrl) // y se procesa para fichar

                    }

                    override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) { // si la respuesta de la api falla
                        employeeInfo.value = EmployeeResponse(
                            status = "error", // se da error
                            null, null, null, null, null, null,
                            message = "connection-error"
                        )
                        errorMessage.value = "Fallo en red: ${t.message}"
                        Log.d("Empleado", "ERROR: ${errorMessage.value} (105)")
                    }
                })
            }
        }
    }

    // Funcion que optimiza el flujo de fichaje
    private fun handleEmployeeFlow(employee: EmployeeResponse, apiKey:String , currentUrl: String) {

        val employeeId = employee.odooId ?: return

        //funcion lambda para conseguir el ultimo fichaje del empleado
        getLastSigning(employeeId, apiKey, currentUrl) { lastResult ->

            if(lastResult != null && lastResult.status == "error"){
                errorMessage.value = "Error con el estado de fichaje"
                Log.d("Empleado", "ERROR: ${errorMessage.value} (229)")
                return@getLastSigning
            }
            val last = lastResult?.type ?: false
            executeSingingLogic(employee, apiKey, currentUrl, last)
        }

    }

    //Funcion que ejecuta el flujo del fichaje
    fun executeSingingLogic(employee: EmployeeResponse, apiKey: String, currentUrl: String, lastResult: Boolean?){
        val employeeId = employee.odooId
        if (employeeId == null) {
            errorMessage.value = "Empleado sin ID válido"
            Log.d("Empleado", "ERROR: ${errorMessage.value} (218)")
            return
        }
        val newSignedState = employee.isSigned

        // llamada a la primera funcion lambda para saber si se ficha tarde
        searchLastSession(apiKey, currentUrl) { sessionResult ->

            if (sessionResult == null) {
                errorMessage.value = "Error comprobando ultima sesion"
                Log.d("Empleado", "ERROR: ${errorMessage.value} (229)")
                return@searchLastSession
            }

            lastSessionInfo.value = sessionResult

            // llamada a la segunda funcion lambda para comprobar si el fichaje es doble
            checkDoubleSigning(employeeId, apiKey, currentUrl) { doubleResult ->

                if (doubleResult == null) {
                    errorMessage.value = "Error comprobando fichaje doble"
                    Log.d("Empleado", "ERROR: ${errorMessage.value} (250)")
                    return@checkDoubleSigning
                }

                if (doubleResult.status != "success") {
                    errorMessage.value = "Error en validación de fichaje"
                    Log.d("Empleado", "ERROR: ${errorMessage.value} (256)")
                    return@checkDoubleSigning
                }

                //  objeto final consistente
                val updatedEmployee = employee.copy(
                    isSigned = !lastResult!!,
                    isDoubleSigned = doubleResult.doubleSigning,
                    isLate = sessionResult.isLate
                )

                doubleSignInfo.value = doubleResult
                employeeInfo.value = updatedEmployee
                errorMessage.value = null

                if (!doubleResult.doubleSigning) {
                    logSigning()
                    saveEmployee(updatedEmployee)
                } else {
                    viewModelScope.launch {
                        _confirmSigning.emit(updatedEmployee)
                    }
                }
            }
        }
    }

    // Cambiamos a suspend y eliminamos el launch interno
    fun getLastSigning(employeeId: Int, apiKey: String, currentUrl: String, onResult: (LastSigning?) -> Unit) {
        val call = repository.searchLastSigning(employeeId, apiKey,currentUrl)

        call.enqueue(object : Callback<LastSigning> {
            override fun onResponse(call: Call<LastSigning>, response: Response<LastSigning>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()) // Avisamos que ya tenemos el dato
                } else {
                    errorMessage.value = "Error servidor: ${response.code()}"
                    Log.d("Empleado", "ERROR: ${errorMessage.value} (316)")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<LastSigning>, t: Throwable) {
                errorMessage.value="Fallo red: ${t.message}"
                Log.d("Empleado", "ERROR: ${errorMessage.value} (323)")
                onResult(null)
            }
        })
    }

    fun onSigningConfirmed() {  //funcion para fichar y cambiar el estado de fichaje

        val employee = employeeInfo.value ?: return

        val corrected = employee.copy(
            isDoubleSigned = false,
            isSigned = !(latestSuccessfulSigning ?: employee.isSigned)
        )

        employeeInfo.value = corrected
        logSigning()
        saveEmployee(corrected)
    }

    //funcion lambda que calcula si el fichaje es doble
    fun checkDoubleSigning(id: Int, apiKey:String, currentUrl:String, onResult: (CheckDoubleSigning?) -> Unit) {

        // se llama a la api que calcula si el fichaje ha sido doble
        repository.checkDoubleSigning(id, apiKey,currentUrl)
            .enqueue(object : Callback<CheckDoubleSigning> {

                override fun onResponse(
                    call: Call<CheckDoubleSigning>,
                    response: Response<CheckDoubleSigning>
                ) {
                    onResult(response.body()) // se devuelve el resultado

                }
                // si da error se devuelve null
                override fun onFailure(call: Call<CheckDoubleSigning>, t: Throwable) {
                    errorMessage.value = "Error de red: ${t.message}"
                    Log.d("Empleado", "ERROR: ${errorMessage.value} (327)")
                    onResult(null)
                }
            })

    }
    //Funcion para guardar en un hashmap la informacion de los empleados
    fun saveEmployee(employee: EmployeeResponse?){
        // se optiene el codigo rfid y el dni del empleado, se comprueba que no sea nulo y se guarda en un hash map
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
        doubleSignInfo.value = null
        lastSessionInfo.value = null
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

    // funcion lambda para comprobar si se ha fichado tarde
    fun searchLastSession(apiKey:String, currentUrl: String, onResult: (LastSession?) -> Unit){
        repository.searchLastSession(apiKey, currentUrl)
            .enqueue(object : Callback<LastSession>{
                override fun onResponse(
                    call: Call<LastSession>,
                    response: Response<LastSession>
                ){
                    // si la respuesta es favorable se devuelve
                    onResult(response.body())
                }

                override fun onFailure( // si no se guarda el error
                    call: Call<LastSession>,
                    t: Throwable
                ){
                    errorMessage.value = "Error de red: ${t.message}"
                    Log.d("Empleado", "ERROR: ${errorMessage.value} (407)")
                    onResult(null)
                }
            })
    }

    //Funcion para enviar datos a la api que registrara los fichajes de los empleados
    fun logSigning() {
        viewModelScope.launch {
            // se captura la URL
            val currentUrl = settingsManager.formattedUrl.first()
            //api key
            val apiKey = settingsManager.apiKey.first()

            //Comprueba que el id y el estado del empleado no sean nulos
            val employee = employeeInfo.value ?: return@launch
            val employeeId = employee.odooId ?: return@launch

            // se captura el nombre del dispositivo y si el fichaje es de entrada o salida
            val deviceName = settingsManager.deviceName.first()
            val isEntry = employee.isSigned
            val apiState = if (isEntry) "I" else "O"

            // se crea el objeto que envia los datos
            val request = OdooRequest(
                params = AttendanceParams(
                    employee_id = employeeId,
                    type = apiState,
                    terminal_id = deviceName,
                    location_id = 1
                )
            )
            Log.d("Empleado", "DEBUG: ${request} (264)")
            try {
                //Llamada a la api para enviarle los datos del empleado
                RetrofitClient.getOdooApi(currentUrl).logAttendance(apiKey,request)
                employee.isSigned = isEntry
            } catch (e: Exception) {
                errorMessage.value = "Error al fichar: ${e.localizedMessage ?: "Fallo desconocido"}"
                Log.d("Empleado", "ERROR: ${e.message} (465)")

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