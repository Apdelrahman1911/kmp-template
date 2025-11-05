// presentation/viewmodel/SourcesViewModel.kt
package me.onvo.onvo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import dev.zacsweers.metro.AppScope
//import dev.zacsweers.metro.Inject
//import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.onvo.onvo.domain.model.Source
import me.onvo.onvo.domain.usecase.GetSourcesUseCase

sealed class SourcesUiState {
    data object Loading : SourcesUiState()
    data class Success(val sources: List<Source>) : SourcesUiState()
    data class Error(val message: String) : SourcesUiState()
}

//@Inject
//@SingleIn(AppScope::class)
class SourcesViewModel(
    private val getSourcesUseCase: GetSourcesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SourcesUiState>(SourcesUiState.Loading)
    val uiState: StateFlow<SourcesUiState> = _uiState.asStateFlow()

    init {
        loadSources()
    }

    fun loadSources() {
        viewModelScope.launch {
            _uiState.value = SourcesUiState.Loading
            getSourcesUseCase()
                .onSuccess { sources ->
                    _uiState.value = SourcesUiState.Success(sources)
                }
                .onFailure { error ->
                    _uiState.value = SourcesUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun retry() {
        loadSources()
    }
}