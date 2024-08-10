package com.example.noteapp.note.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.DOCUMENT_SCREEN_ARGUMENT_ID
import com.example.noteapp.MANAGE_ACCESS_SCREEN_ARGUMENT_ID
import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.domain.repository.DatabaseClient2
import com.example.noteapp.note.domain.repository.RealtimeDatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject


@HiltViewModel
class ManageAccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dbClient: DatabaseClient2
) : ViewModel() {

    private val _docId = MutableStateFlow(
        savedStateHandle.get<String>(MANAGE_ACCESS_SCREEN_ARGUMENT_ID)
    )
    val docId = _docId.asStateFlow()
    private val _state = MutableStateFlow(ManageAccessState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _docId.value?.let {
                dbClient.getRealtimeShareHolderOfGivenDocument(it) { data ->
                    updatePeopleWithAccess(data)
                }
            }
        }
    }

    fun onClickPermission(shareHolder: ShareHolder, permission: Permission) {
        viewModelScope.launch {
            _state.update { it.copy(showBottomSheet = false) }
            dbClient.updateSharedPermission(shareHolder, permission)
            refreshShareholders()
        }
    }

    fun onClickPeople(shareHolder: ShareHolder) {
        _state.update {
            it.copy(
                showBottomSheet = true,
                selectedPeople = shareHolder,
            )
        }
    }

    fun onDismissRequestBottomSheet() {
        _state.update { it.copy(showBottomSheet = false) }
    }

    fun onClickRemovePeople(shareHolder: ShareHolder) {
        viewModelScope.launch {
            _state.update { it.copy(showBottomSheet = false) }
            dbClient.deleteSharedPermission(shareHolder) {

                refreshShareholders()
            }
        }
    }

    private fun refreshShareholders() {
        viewModelScope.launch {

            _docId.value?.let {
                dbClient.getRealtimeShareHolderOfGivenDocument(it) { data ->
                    updatePeopleWithAccess(data)
                }
            }
        }
    }

    private fun updatePeopleWithAccess(data: List<ShareHolder>) {
        // Remove duplicates by using a set to track seen ids
        val uniqueShareholders = data.distinctBy { it.sharedId }
        _state.update { it.copy(peopleWithAcess = uniqueShareholders) }
        // Log the updated state for debugging
        println("Updated peopleWithAcess: ${_state.value.peopleWithAcess}")
    }
}

data class ManageAccessState(
    var peopleWithAcess: List<ShareHolder> = emptyList(),
    val showBottomSheet: Boolean = false,
    val selectedPeople: ShareHolder? = null,
)
