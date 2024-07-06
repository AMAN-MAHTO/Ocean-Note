package com.example.noteapp.note.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.noteapp.DOCUMENT_SCREEN_ARGUMENT_ID
import com.example.noteapp.Permission
import com.example.noteapp.Screen
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.repository.DatabaseClient2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import okhttp3.internal.wait

import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val dbClient: DatabaseClient2,
    savedStateHandle: SavedStateHandle,
    googleAuthUiClient: GoogleAuthUiClient
) :ViewModel(){
    private val _docId = MutableStateFlow(savedStateHandle.get<String>(DOCUMENT_SCREEN_ARGUMENT_ID))
    val docId = _docId.asStateFlow()

    private val _doc = MutableStateFlow(Document())
    val doc = _doc.asStateFlow()

    private val _state = MutableStateFlow(
    DocumentState(
            document = _doc.value
        )
    )
    val state = _state.asStateFlow()

    init {

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            _state.value = _state.value.copy(
                userData = googleAuthUiClient.getSignedInUser()
            )
            if(_docId.value != null){

            dbClient.getRealtimeDocumentById(
                id = _docId.value!!,
                listener = {
                    _doc.value = it
                    _state.value = _state.value.copy(
                        document = _doc.value,

                    )
                    if (_state.value.userData != null) {
                        if (_state.value.document.ownerId == _state.value.userData!!.userId) {
                            Log.d("Document", "permission all")

                            _state.value = _state.value.copy(permission = Permission.ALL, isLoading = false)
                        } else {
                            Log.d("Document", "permission read or write")
                            Log.d("Document", "userdata: ${_state.value}")
                            viewModelScope.launch {

                                dbClient.getSharedCard(_state.value.document.id) {
                                    if (it.permissionType == "READ") {
                                        _state.value = _state.value.copy(
                                            permission = Permission.READ,
                                            isLoading = false
                                        )
                                    } else {
                                        _state.value = _state.value.copy(
                                            permission = Permission.WRITE,
                                            isLoading = false
                                        )
                                    }

                                }
                            }

                        }
                    }
                }
                )


            }

        }


    }

    fun onTitleChange(title: String){
        _state.value = _state.value.copy(
            document = _state.value.document.copy(title = title, updatedAt = System.currentTimeMillis())
        )
        viewModelScope.launch {

        dbClient.updateDocument(_state.value.document)
        }
    }
    fun onBodyChange(body: String){
        _state.value = _state.value.copy(
            document = _state.value.document.copy(body = body, updatedAt = System.currentTimeMillis())
        )
        viewModelScope.launch {

            dbClient.updateDocument(_state.value.document)
        }
    }

    fun onClickDelete(
    ) {
        _state.value = _state.value.copy(
            openDeleteAlertDialog = true
        )
    }

    fun onDeleteAlertDialogConfirmation(navHostController: NavHostController,) {

        if(state.value.permission == Permission.ALL){
            viewModelScope.launch {

                dbClient.deleteDocumentById(_docId.value!!)
                _state.value = _state.value.copy(
                    openDeleteAlertDialog = false
                )
                navHostController.popBackStack()
            }
        }
    }

    fun onDeleteAlertDialogDismissRequest() {
        _state.value = _state.value.copy(
            openDeleteAlertDialog = false
        )
    }

    fun onClickShare() {
        _state.value = _state.value.copy(
            openShareDialog = true
        )    }

    fun onDismissShareDialogRequest() {
        _state.value = _state.value.copy(
            openShareDialog = false
        )
    }

}
data class DocumentState(
    var document: Document,
    var userData: UserData? = null,
    var permission : Permission = Permission.READ,
    var isLoading: Boolean = true,
    var openDeleteAlertDialog: Boolean = false,
    var openShareDialog:Boolean = false,
    )