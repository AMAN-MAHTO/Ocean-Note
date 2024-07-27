package com.example.noteapp.note.presentation.view_models

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.noteapp.DOCUMENT_SCREEN_ARGUMENT_ID
import com.example.noteapp.Permission
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.data.TAG
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.EditorRelamChild
import com.example.noteapp.note.domain.repository.DatabaseClient2
import com.example.noteapp.note.domain.repository.RealtimeDatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val dbClient: DatabaseClient2,
    private val rdbClient: RealtimeDatabaseClient,
    savedStateHandle: SavedStateHandle,
    googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {
    private val _docId = MutableStateFlow(savedStateHandle.get<String>(DOCUMENT_SCREEN_ARGUMENT_ID))
    val docId = _docId.asStateFlow()

    private val _state = MutableStateFlow(
        DocumentState(
            document = Document()
        )
    )
    val state = _state.asStateFlow()
    var _isEditorAdded = false
    var _isChancesMade = false

    init {

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true
            )
            _state.value = _state.value.copy(
                userData = googleAuthUiClient.getSignedInUser()
            )

            _docId.value?.let {
                dbClient.getRealtimeDocumentById(it) { document ->
                    _state.value.document = document
                    viewModelScope.launch {
                        _docId.value?.let {
                            if (!_isEditorAdded) {
                                Log.d(TAG, "editor added: ")
                                dbClient.addEditor(document)
                                _isEditorAdded = true
                            }

                            rdbClient.getRealtimeEditorRealm(document) { editorRealmChild ->
                                _state.value = _state.value.copy(
                                    document = _state.value.document.copy(

                                        title = editorRealmChild.title,
                                        body = editorRealmChild.body,
                                        currentEditors = editorRealmChild.currentEditors,
                                    )
                                )
                            }

                        }
                    }
                    if (_state.value.userData != null) {
                        if (_state.value.document.ownerId == _state.value.userData!!.userId) {
                            Log.d("Document", "permission all")

                            _state.value =
                                _state.value.copy(permission = Permission.ALL, isLoading = false)

                        } else {
                            Log.d("Document", "permission read or write")
                            Log.d("Document", "userdata: ${_state.value}")
                            viewModelScope.launch {

                                dbClient.getSharedCard(_state.value.document.id) {
                                    if (it.permissionType == Permission.READ.toString()) {
                                        _state.value = _state.value.copy(
                                            document = document,
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
            }


        }


    }


    fun onTitleChange(title: String) {
        _isChancesMade = true
        _state.value = _state.value.copy(
            document = _state.value.document.copy(
                title = title,
                updatedAt = System.currentTimeMillis()
            )
        )
        viewModelScope.launch {

            val doc = _state.value.document
            rdbClient.updateEditorRealm(
                EditorRelamChild(
                    doc.id,
                    doc.title,
                    doc.body,
                    doc.currentEditors
                )
            )
        }
    }

    fun onBodyChange(body: String) {
        _isChancesMade = true
        _state.value = _state.value.copy(
            document = _state.value.document.copy(
                body = body,
                updatedAt = System.currentTimeMillis()
            )
        )
        viewModelScope.launch {

            val doc = _state.value.document
            rdbClient.updateEditorRealm(
                EditorRelamChild(
                    doc.id,
                    doc.title,
                    doc.body,
                    doc.currentEditors
                )
            )
        }
    }

    fun onCloseOrSave(onClickBack: () -> Unit) {
        viewModelScope.launch {

            if (_isEditorAdded) {
                dbClient.updateDocument(_state.value.document)
                Log.d(TAG, "onCloseOrSave: updating doc done")

                rdbClient.removeEditorRealmListener(_state.value.document)
                Log.d(TAG, "onCloseOrSave: remove editor realm")

                dbClient.removeEditor(_state.value.document) {
                    Log.d(TAG, "onCloseOrSave: remove editor")
                    if (_isChancesMade) {

                        viewModelScope.launch {
                            dbClient.addVersion(_state.value.document)
                            Log.d(TAG, "onCloseOrSave: adding version")

//                        rdbClient.removeEditorRealm(_state.value.document)
//                        Log.d(TAG, "onCloseOrSave: removing realm")

                        }
                    }
                }

                onClickBack()
            } else {
                onClickBack()
            }


        }
    }

    fun onClickDelete(
    ) {
        _state.value = _state.value.copy(
            openDeleteAlertDialog = true
        )
    }

    fun onDeleteAlertDialogConfirmation(navHostController: NavHostController) {

        if (state.value.permission == Permission.ALL) {
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
        )

    }

    fun onDismissShareDialogRequest() {
        _state.value = _state.value.copy(
            openShareDialog = false
        )
    }

}

data class DocumentState(
    var document: Document,
    var userData: UserData? = null,
    var permission: Permission = Permission.READ,
    var isLoading: Boolean = true,
    var openDeleteAlertDialog: Boolean = false,
    var openShareDialog: Boolean = false,
)


//if(_docId.value != null){
//
//    dbClient.getRealtimeDocumentById(
//        id = _docId.value!!,
//        listener = { document->
//            if(document.currentEditors.isNotEmpty()){
//                viewModelScope.launch {
//
//                    rdbClient.getRealtimeVersion(document.id){
//                        Log.d(TAG1, "addVersionListener: $it")
//                        _state.value = _state.value.copy(
//                            document = it
//                        )
//                    }
//                }
//            }else{
//                _state.value = _state.value.copy(
//                    document = document
//                )
//            }
//            if (_state.value.userData != null) {
//                if (_state.value.document.ownerId == _state.value.userData!!.userId) {
//                    Log.d("Document", "permission all")
//
//                    _state.value = _state.value.copy(permission = Permission.ALL, isLoading = false)
//
//                } else {
//                    Log.d("Document", "permission read or write")
//                    Log.d("Document", "userdata: ${_state.value}")
//                    viewModelScope.launch {
//
//                        dbClient.getSharedCard(_state.value.document.id) {
//                            if (it.permissionType == Permission.READ.toString()) {
//                                _state.value = _state.value.copy(
//                                    document = document,
//                                    permission = Permission.READ,
//                                    isLoading = false
//                                )
//                            } else {
//                                _state.value = _state.value.copy(
//                                    permission = Permission.WRITE,
//                                    isLoading = false
//                                )
//
//
//                            }
//
//                        }
//                    }
//
//                }
//            }
//        }
//    )
//
//}