package com.example.mayatimegate.viewmodel

import android.util.Log
import androidx.collection.doubleListOf
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

            val cachedEmployee = getEmployeeFromHashMap(rfid) //se busca el empleado en el HashMap

            if(cachedEmployee != null) { //Si no se encuentra el empleado en memoria
                cachedEmployee.changeSignedState()
                handleEmployeeFlow(cachedEmployee)
            }else{

                //Llamada al repositorio para buscar al empleado
                val call = repository.searchEmployeeByRfid(rfid, currentUrl)

                call.enqueue(object : Callback<EmployeeResponse>{

                    override fun onResponse( //Funcion que da valor al empleado
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (!response.isSuccessful) { //si la respuesta es erronea se guarda el error
                            errorMessage.value = "Error en el servidor: ${response.code()}"
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
                            return
                        }

                        serverResponse.rfid = rfid//se guarda el codigo rfid
                        serverResponse.changeSignedState() //se cambia el estado
                        saveEmployee(serverResponse) //se guarda el empleado
                        handleEmployeeFlow(serverResponse) //se procede al flujo de fichaje


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

            val currentUrl = settingsManager.formattedUrl.first() //se consige la URL

            // se valida la URL
            if (!isValidBaseUrl(currentUrl)) {
                employeeInfo.value = EmployeeResponse( //si la URL no cumple con el formato se da error
                    status = "error",
                    null, null, null, null, null, null,
                    message = "connection-error"
                )
                errorMessage.value = "URL inválida. Revise la configuración"
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
                return@launch
            }
            val officialDni = inputDni + calculateLetterOfDni(dniNumber)

            //se busca si el empleado exite en memoria
            val cachedEmployee = getEmployeeFromHashMap(officialDni)
            if (cachedEmployee != null) { //si no es nulo es que existe en memoria
                cachedEmployee.changeSignedState()  //se cambia el estado del empleado
                handleEmployeeFlow(cachedEmployee) // y se procesa al empleado para fichar
            } else { //si no esta en memoria se busca en la api
                val call = repository.searchEmployeeByDni(officialDni, currentUrl) //se devuelve informacionde la api

                call.enqueue(object : Callback<EmployeeResponse> {

                    override fun onResponse( //si la llamada a la api ha encontrado informacion
                        call: Call<EmployeeResponse>,
                        response: Response<EmployeeResponse>
                    ) {
                        if (!response.isSuccessful) { // si el estado no es satisfactorio
                            errorMessage.value = "Error en el servidor: ${response.code()}" //se da error
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
                            return
                        }
                        serverResponse.changeSignedState()
                        saveEmployee(serverResponse) // se guarda el empleado en memoria
                        handleEmployeeFlow(serverResponse) // y se procesa para fichar

                    }

                    override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) { // si la respuesta de la api falla
                        employeeInfo.value = EmployeeResponse(
                            status = "error", // se da error
                            null, null, null, null, null, null,
                            message = "connection-error"
                        )
                        errorMessage.value = "Fallo en red: ${t.message}"
                    }
                })
            }
        }
    }

    // Funcion que optimiza el flujo de fichaje
    private fun handleEmployeeFlow(employee: EmployeeResponse) {

        val employeeId = employee.odooId
        if (employeeId == null) {
            errorMessage.value = "Empleado sin ID válido"
            return
        }
        val newSignedState = employee.isSigned

        checkDoubleSigning(employeeId) { result -> // funcion lambda que calcula el doble fichaje
            // devuelve el resultado del doble fichaje
            if (result == null) { //si el resultado es nulo devuelve error
                errorMessage.value = "Error comprobando fichaje doble"
                return@checkDoubleSigning
            }

            if (result.status != "success") { //Si el resultado no es satisfactorio se devuelve error
                errorMessage.value = "Error en validación de fichaje"
                return@checkDoubleSigning
            }

            // Crear objeto consistente antes de emitir
            val updatedEmployee = employee.copy(
                isSigned = newSignedState,
                isDoubleSigned = result.doubleSigning
            )

            doubleSignInfo.value = result // se establece la variable que guarda si ha habido doble fichaje

            employeeInfo.value = updatedEmployee // se actualiza el empleado
            errorMessage.value = null

            if (!result.doubleSigning) {
                logSigning()
                saveEmployee(updatedEmployee)
            } else {
                viewModelScope.launch {
                    _confirmSigning.emit(updatedEmployee)
                }
            }
        }
    }
    fun onSigningConfirmed() {

        val employee = employeeInfo.value ?: return
        val corrected = employee.copy(
            isDoubleSigned = false
        )
        if(latestSuccessfulSigning != null){
            corrected.isSigned = !latestSuccessfulSigning!!
        }
        employeeInfo.value = corrected
        logSigning()
        saveEmployee(corrected)
    }

    //funcion lambda que calcula si el fichaje es doble
    fun checkDoubleSigning(id: Int, onResult: (CheckDoubleSigning?) -> Unit) {

        viewModelScope.launch {
            // se optiene el la URL y se valida que sea correcta
            val currentUrl = settingsManager.formattedUrl.first()

            if (!isValidBaseUrl(currentUrl)) {
                errorMessage.value = "URL inválida"
                onResult(null)
                return@launch
            }
            // se llama a la api que calcula si el fichaje ha sido doble
            repository.checkDoubleSigning(id, currentUrl)
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
                        onResult(null)
                    }
                })
        }
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
    fun logSigning() {
        viewModelScope.launch {
            // se captura la URL
            val currentUrl = settingsManager.formattedUrl.first()
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

            try {
                //Llamada a la api para enviarle los datos del empleado
                RetrofitClient.getOdooApi(currentUrl).logAttendance(request)
                employee.isSigned = isEntry
            } catch (e: Exception) {
                println("Error al guardar los datos en attendance: ${e.message}")

            }
            Log.d("HOLA", "final: ${employeeInfo.value}")
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








/*
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
                val cacheEmployee = getEmployeeFromHashMap(officialDni)
                val employee = cacheEmployee
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
                                //

                                if (serverResponse != null && serverResponse.status == "success") {
                                    serverResponse.changeSignedState()
                                    employeeInfo.value = serverResponse
                                    saveEmployee(employeeInfo.value)
                                    //employeeInfo.value?.changeSignedState()

                                    val employeeId = employeeInfo.value?.odooId
                                    val employeeState = employeeInfo.value?.isSigned
                                    checkDoubleSigning(employeeId!!) { result ->

                                        if (result?.status == "success") {

                                            logSigning(employeeId, employeeState)
                                            errorMessage.value = null
                                            doubleSignInfo.value = result
                                            Log.i("PROBLEMA", "Estado 1: "+employeeInfo.value!!.isDoubleSigned)
                                            Log.i("PROBLEMA", "Fichaje doble "+doubleSignInfo.value)
                                            employeeInfo.value!!.isDoubleSigned = doubleSignInfo.value!!.doubleSigning
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
                            Log.i("PROBLEMA", "Estado 2: "+employeeInfo.value!!.isDoubleSigned )
                            Log.i("PROBLEMA", "Fichaje doble "+doubleSignInfo.value)
                            employeeInfo.value!!.isDoubleSigned = doubleSignInfo.value!!.doubleSigning
                            Log.i("PROBLEMA", "Fichaje doble "+doubleSignInfo.value!!.doubleSigning)
                            val a = doubleSignInfo.value!!.doubleSigning
                            Log.i("PROBLEMA", "a: $a  ${employeeInfo.value}")

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
 */

/*
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
                                checkDoubleSigning(employeeId!!) { result ->

                                    if (result?.status == "success") {
                                        logSigning()
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
                checkDoubleSigning(employeeId!!) { result ->
                    if (result?.status == "success") {
                        logSigning()
                        errorMessage.value = null
                        doubleSignInfo.value = result
                    }
                }
            }
        }
    }

*/