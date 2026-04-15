package com.example.mayatimegate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mayatimegate.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsManager) : ViewModel() {

    val deviceName = repository.deviceName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Cargando..."
    )

    fun updateDeviceName(newName: String) {
        viewModelScope.launch { repository.setDeviceName(newName) }
    }

    val urlName = repository.urlName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Cargando..."
    )

    fun updateUrlName(newName: String) {
        viewModelScope.launch { repository.setUrl(newName) }
    }
}