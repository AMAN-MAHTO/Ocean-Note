package com.example.noteapp.note.presentation.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.noteapp.Permission
import com.example.noteapp.Screen
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.domain.models.Shared
import com.example.noteapp.note.domain.repository.DatabaseClient2
import com.example.noteapp.note.domain.repository.RealtimeDatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentListViewModel @Inject constructor(
    private val dbClient: DatabaseClient2,
    private val rdbClient: RealtimeDatabaseClient,
    private val googleAuthUiClient: GoogleAuthUiClient,

    ) : ViewModel() {


    private val _state =
        MutableStateFlow(DocumentListState(userData = googleAuthUiClient.getSignedInUser()))
    val state = _state.asStateFlow()

    init {
        _state.update {
            DocumentListState(userData = googleAuthUiClient.getSignedInUser())
        }
        Log.d("Document", "owned doc: ${_state.value.userData}")

        viewModelScope.launch {


            _state.value.userData?.let {
                dbClient.getRealtimeDocumentOwned(userData = it) { documents ->
                    //                Log.d("Document", "owned doc: $documents")

                    _state.update {
                        it.copy(
                            ownedDocuments = documents
                        )
                    }
                }
            }

            _state.value.userData?.let {
                dbClient.getRealtimeSharedDocumentOfCurrentUser(
                    userData = it,
                    sharedListner = { documents ->
                        //                    Log.d("Document", "shared card: $documents")
                        _state.update {
                            it.copy(
                                sharedCardList = documents
                            )
                        }
                    },
                    docListner = { documents ->
                        //                    Log.d("Document", "shared doc: $documents")
                        _state.update {
                            it.copy(
                                sharedDocuments = documents
                            )
                        }
                    })
            }


        }
    }

    fun onClickFAB(navHostController: NavHostController) {
        Log.d("Document", "onClickFAB ")

        viewModelScope.launch {
            navHostController.navigate(Screen.Document.setId(""))


        }

    }


    fun onDismissProfileDialogRequest() {
        _state.update {
            it.copy(

                isProfileView = false
            )
        }
    }

    fun onProfileDialogRequest() {
        _state.update {
            it.copy(
                isProfileView = true
            )
        }
    }

    fun onLogout(navHostController: NavHostController) {
        viewModelScope.launch {

            googleAuthUiClient.signOut()
            _state.update {
                it.copy(
                    isProfileView = false
                )
            }
            navHostController.navigate(Screen.SignIn.route) {
                popUpTo(navHostController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    fun onClickShortByItem(filterDoc: FilterDoc) {
        _state.update {
            it.copy(
                filter = filterDoc,
                showSortBySheet = false

            )
        }
    }

    fun onDismissShortBySheet() {
        _state.update {
            it.copy(
                showSortBySheet = false
            )
        }
    }

    fun onClickshortBy() {
        _state.update {
            it.copy(
                showSortBySheet = true
            )
        }
    }

    fun onLongClickDoc(docId: String) {
        if (_state.value.ownedDocuments.any { it.id == docId })
            _state.update {
                it.copy(
                    showDocActionSheet = true,
                    docActionSheetId = docId,
                )
            }

    }

    fun onDismissDocActionSheet() {
        _state.update {
            it.copy(
                showDocActionSheet = false,
                docActionSheetId = null,
            )
        }
    }

    fun onClickDocActionItem(docActions: DocActions, navHostController: NavHostController) {
        _state.update {
            it.copy(
                showDocActionSheet = false,
            )
        }
        if (_state.value.ownedDocuments.any { it.id == _state.value.docActionSheetId }) {
            when (docActions) {
                DocActions.SHARE -> viewModelScope.launch {
                    _state.value.docActionSheetId?.let {
                        navHostController.navigate(
                            Screen.Share.setId(
                                it
                            )
                        )
                    }
                }

                DocActions.MANAGE_ACCESS -> viewModelScope.launch {
                    _state.value.docActionSheetId?.let {
                        navHostController.navigate(
                            Screen.ManageAccess.setId(
                                it
                            )
                        )
                    }
                }

                DocActions.DELETE -> _state.update {
                    it.copy(
                        showDeleteAlertBox = true
                    )
                }
            }
        }

    }

    fun onDismissDeleteActionDialog() {
        _state.update {
            it.copy(
                showDeleteAlertBox = false
            )
        }
    }

    fun onDeleteAlertConformation() {
        if (_state.value.ownedDocuments.any { it.id == _state.value.docActionSheetId }) {
            viewModelScope.launch {

                dbClient.deleteDocumentById(_state.value.docActionSheetId!!)
                _state.update {
                    it.copy(
                        showDeleteAlertBox = false
                    )
                }
            }
        }
    }


//
}

data class DocumentListState(
    val ownedDocuments: List<Document> = emptyList(),
    val sharedDocuments: List<Document> = emptyList(),
    val sharedCardList: List<Shared> = emptyList(),
    val isProfileView: Boolean = false,
    val userData: UserData?,
    val filter: FilterDoc = FilterDoc.LAST_UPDATED,
    val showSortBySheet: Boolean = false,
    val showDocActionSheet: Boolean = false,
    val docActionSheetId: String? = null,
    val showDeleteAlertBox: Boolean = false,
)

enum class FilterDoc {
    LAST_UPDATED, NAME
}

enum class DocActions {
    SHARE, MANAGE_ACCESS, DELETE
}

fun generateFakeDocuments(): List<Document> {
    val documents = mutableListOf<Document>()
    for (i in 1..3) {
        val document = Document(
            id = "",
            title = "title${i + 3}",
            body = "body${i + 3}",
            ownerId = "FKy3vlHgiPhR7PGv7JOcWH908so1",
            currentEditors = emptyList(),
            lastEditTime = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            sharedIdList = emptyList(),
        )
        documents.add(document)
    }
    return documents
}

