package com.example.mayatimegate.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mayatimegate.data.RetrofitClient
import com.example.mayatimegate.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider

class SettingsViewModel( //SettingsViewModel
    application: Application,
    private val repository: SettingsManager
) : AndroidViewModel(application) {


    val deviceName = repository.deviceName.stateIn( //Variable que guarda el nombre del dispositivo
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Cargando..."
    )

    fun updateDeviceName(newName: String) { //Funcion que actualiza el nombre del dispositivo
        viewModelScope.launch { repository.setDeviceName(newName) }
    }

    val adminPass = repository.adminPass.stateIn( //variable que guarda la url de odoo
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Cargando..."
    )

    fun updateAdminPass(newName: String) { //Funcion que actualiza la url de odoo
        viewModelScope.launch { repository.setAdminPass(newName) }
    }

    val urlName = repository.urlName.stateIn( //variable que guarda la url de odoo
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Cargando..."
    )

    fun updateUrlName(newName: String) { //Funcion que actualiza la url de odoo
        viewModelScope.launch { repository.setUrl(newName) }
    }

    //Android ID
    @SuppressLint("HardwareIds")//Advertencia de que si se formatea cambia el id
    val androidId: String = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "Desconocido"

    fun realizarAccionEnOdoo() {
        viewModelScope.launch {
            try {
                // Obtenemos la API con la URL actual del StateFlow
                val api = RetrofitClient.getOdooApi(urlName.value)

                // Aquí podrías enviar el androidId en la petición si Odoo lo requiere
                // val response = api.login(androidId, ...)
            } catch (e: Exception) {
                // Manejar error de conexión
            }
        }
    }
}

//Clase factory que crea una instancia de EmployeeViewModel
class SettingsViewModelFactory(
    private val application: Application,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            //Si es SettingsView deolvemos la instancia
            return SettingsViewModel(application, settingsManager) as T
        }
        //Si hay alguna excepcion
        throw IllegalArgumentException("Clase viewModel desconocida")
    }
}