package com.example.noteapp.note.presentation.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentListViewModel @Inject constructor(
    private val dbClient: DatabaseClient2,
    private val rdbClient: RealtimeDatabaseClient,
    private val googleAuthUiClient: GoogleAuthUiClient,

    ) : ViewModel() {

    private val _userData = googleAuthUiClient.getSignedInUser()

    private val _ownedDocuments = MutableStateFlow(emptyList<Document>())
    val ownedDocuments = _ownedDocuments.asStateFlow()

    private val _sharedDocument = MutableStateFlow(emptyList<Document>())
    val sharedDocument = _sharedDocument.asStateFlow()

    private val _sharedCardList = MutableStateFlow(listOf<Shared>())

    private val _isProfileView = MutableStateFlow(false)
    val isProfileView = _isProfileView.asStateFlow()

    init {
        viewModelScope.launch {
            dbClient.getRealtimeDocumentOwned {
                Log.d("Document", "owned doc: $it")
                _ownedDocuments.value = emptyList()
                _ownedDocuments.value = it
            }

            dbClient.getRealtimeSharedDocumentOfCurrentUser(
                sharedListner = {
                    _sharedCardList.value = emptyList()
                    _sharedCardList.value = it
                },
                docListner = {
                    Log.d("Document", "shared doc: $it")
                    _sharedDocument.value = emptyList()
                    _sharedDocument.value = it

                })

        }
    }

    fun onClickFAB(navHostController: NavHostController) {
        Log.d("Document", "onClickFAB ")

        viewModelScope.launch {
            val docId = dbClient.addDocument(Document())

            Log.d("Document", "onClickFAB: new doc id ${docId}")

            navHostController.navigate(Screen.Document.setId(docId))
        }

    }

    fun getUserData(): UserData? {
        return _userData
    }

    fun onDismissProfileDialogRequest() {
        _isProfileView.value = false
    }

    fun onProfileDialogRequest() {
        _isProfileView.value = true
    }

    fun onLogout() {
        viewModelScope.launch {

            googleAuthUiClient.signOut()
            _isProfileView.value = false
        }
    }


//
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

