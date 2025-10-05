package com.dreamsoft.desoft20.ui.screens.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamsoft.desoft20.data.models.AppConfiguration
import com.dreamsoft.desoft20.data.repositories.ConfigurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfigurationUiState(
    val localUrl: String = "",
    val remoteUrl: String = "",
    val printerName: String = "",
    val useLocalUrl: Boolean = false,
    val enableCache: Boolean = false,
    val enableZoom: Boolean = false,
    val enableWebNavigation: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState: StateFlow<ConfigurationUiState> = _uiState.asStateFlow()

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val config = configurationRepository.getConfiguration()
                _uiState.value = _uiState.value.copy(
                    localUrl = config.localUrl,
                    remoteUrl = config.remoteUrl,
                    printerName = config.printerName,
                    useLocalUrl = config.useLocalUrl,
                    enableCache = config.enableCache,
                    enableZoom = config.enableZoom,
                    enableWebNavigation = config.enableWebNavigation,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateLocalUrl(url: String) {
        _uiState.value = _uiState.value.copy(localUrl = url)
    }

    fun updateRemoteUrl(url: String) {
        _uiState.value = _uiState.value.copy(remoteUrl = url)
    }

    fun updatePrinterName(name: String) {
        _uiState.value = _uiState.value.copy(printerName = name)
    }

    fun updateUseLocalUrl(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(useLocalUrl = enabled)
    }

    fun updateEnableCache(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableCache = enabled)
    }

    fun updateEnableZoom(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableZoom = enabled)
    }

    fun updateEnableWebNavigation(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableWebNavigation = enabled)
    }

    fun saveConfiguration() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val config = AppConfiguration(
                    localUrl = _uiState.value.localUrl,
                    remoteUrl = _uiState.value.remoteUrl,
                    printerName = _uiState.value.printerName,
                    useLocalUrl = _uiState.value.useLocalUrl,
                    enableCache = _uiState.value.enableCache,
                    enableZoom = _uiState.value.enableZoom,
                    enableWebNavigation = _uiState.value.enableWebNavigation
                )
                configurationRepository.saveConfiguration(config)
                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                // Handle error - could show toast
            }
        }
    }

    fun resetToDefault() {
        _uiState.value = ConfigurationUiState()
    }
}