package com.example.noteapp.note.presentation.view_models

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.noteapp.Permission
import com.example.noteapp.SHARE_SCREEN_ARGUMENT_ID
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.domain.models.Shared
import com.example.noteapp.note.domain.repository.DatabaseClient2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareScreenViewModel @Inject constructor(
    private val dbClient: DatabaseClient2,
    savedStateHandle: SavedStateHandle,
    googleAuthUiClient: GoogleAuthUiClient,

    ) : ViewModel() {
    private val _docId = MutableStateFlow(savedStateHandle.get<String>(SHARE_SCREEN_ARGUMENT_ID))
    val docId = _docId.asStateFlow()
    fun onQueryChange(s: String) {
        _state.update {
            it.copy(
                query = s,

                )
        }
        if (s.isEmpty()) {
            _state.update {
                it.copy(
                    query = s,
                    result = emptyList()
                )
            }
        } else {

            viewModelScope.launch {
                dbClient.getRealtimeSearch(_state.value.query) { result ->
                    _state.update {
                        it.copy(
                            result = result
                        )
                    }
                }
                Log.d("Search", "onQueryChange: ${_state.value}")
            }
        }
    }

    fun onClickResultElement(element: copyUser?) {
        if (element != null) {
            if (element in _state.value.selectedElement) {
                _state.update {
                    it.copy(
                        selectedElement = it.selectedElement.toMutableList().apply {
                            remove(element)
                        }
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        selectedElement = it.selectedElement.toMutableList().apply {
                            add(element)
                        },
                        query = "",
                        result = emptyList()
                    )
                }
            }


        }

    }

    fun onClickShare(navHostController: NavHostController) {
        viewModelScope.launch {
            for (i in _state.value.selectedElement) {
                if (i.email != null) {
                    _state.value.userData?.let {
                        dbClient.addShared(
                            Shared(
                                sharedWith = i.email!!,
                                documentId = _docId.value ?: "",
                                permissionType = _state.value.permissionType.toString(),
                            ),
                            it
                        )
                    }


                }
            }
            navHostController.popBackStack()
        }
    }

    fun onClickSelecteElementClose(element: copyUser) {
        val list = _state.value.selectedElement.toMutableList()
        list.remove(element)
        _state.update {
            it.copy(
                selectedElement = list,
                query = "",
                result = emptyList()
            )
        }
    }

    fun onClickDropDownPermission(shareHolder: ShareHolder, permission: Permission) {
        viewModelScope.launch {

            dbClient.updateSharedPermission(shareHolder, permission)
        }
    }

    fun changeStatePermission(permission: Permission) {
        _state.update {
            it.copy(
                permissionType = permission
            )
        }
    }

    private val _state =
        MutableStateFlow(ShareDialogState(userData = googleAuthUiClient.getSignedInUser()))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _docId.value?.let {

                dbClient.getRealtimeShareHolderOfGivenDocument(it) { peopleWithAccess ->
                    _state.update {
                        it.copy(
                            peopleWithAcess = peopleWithAccess
                        )
                    }
                }
            }
        }

    }

}

data class ShareDialogState(
    var query: String = "",
    val userData: UserData? = null,
    var peopleWithAcess: List<ShareHolder> = emptyList(),
    var result: List<copyUser> = emptyList(),
    var selectedElement: MutableList<copyUser> = mutableListOf(),
    var permissionType: Permission = Permission.READ,
)