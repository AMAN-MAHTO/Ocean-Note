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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dbClient: DatabaseClient2
) : ViewModel() {


    private val _docId = MutableStateFlow(
        savedStateHandle.get<String>(
            MANAGE_ACCESS_SCREEN_ARGUMENT_ID
        )
    )
    val docId = _docId.asStateFlow()
    val _state = MutableStateFlow(ManageAccessState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _docId.value?.let {

                dbClient.getRealtimeShareHolderOfGivenDocument(it) { data ->
                    _state.value = _state.value.copy(
                        peopleWithAcess = data
                    )
                }
            }
        }

    }

    fun onClickPermission(shareHolder: ShareHolder, permission: Permission) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                showBottomSheet = false
            )
            dbClient.updateSharedPermission(shareHolder,permission)
            _docId.value?.let {
                dbClient.getRealtimeShareHolderOfGivenDocument(it) {
                    _state.value = _state.value.copy(
                        peopleWithAcess = it
                    )
                }
            }
        }

    }

    fun onClickPeople(shareHolder: ShareHolder) {
        _state.value = _state.value.copy(
            showBottomSheet = true,
            selectedPeople = shareHolder,
        )

    }

    fun onDismissRequestBottomSheet() {
        _state.value = _state.value.copy(
            showBottomSheet = false
        )
    }

    fun onClickRemovePeople(shareHolder: ShareHolder) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                showBottomSheet = false
            )
            dbClient.deleteSharedPermission(shareHolder)
            _state.update {
                _state.value.copy(
                    peopleWithAcess = listOf(),
                )
            }
        }
    }
}

data class ManageAccessState(
    var peopleWithAcess: List<ShareHolder> = emptyList(),
    val showBottomSheet: Boolean = true,
    val selectedPeople: ShareHolder = ShareHolder("1", "2", email = "amanmahto@gmail.com"),
)