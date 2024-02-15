package com.example.noteapp.screens.note

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.example.noteapp.EDIT_NOTE_ARGUMENT_ID
import com.example.noteapp.models.NoteData
import com.example.noteapp.services.DatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    // as navcontroller is creating this viewmodel, we use it's NavBackStackEntry to excess argument passed during navigation to note_screen
    savedStateHandle: SavedStateHandle,
    private  val databaseClient: DatabaseClient
):ViewModel() {

    private val _readOnly = MutableStateFlow(true)
    val readOnly = _readOnly.asStateFlow()

    private val _noteId = MutableStateFlow(savedStateHandle.get<String>(EDIT_NOTE_ARGUMENT_ID))
    private val _note = MutableStateFlow(NoteData())
    val note = _note.asStateFlow()
    init {
        viewModelScope.launch {
            val id = _noteId.value
            if(id?.isNotEmpty() == true){
                // fetching note from firestore, it it is null, note value is default NoteData
                _note.value = databaseClient.getNoteById(id)?: NoteData()
            }
        }
    }


    fun toogleReadOnly(){
        _readOnly.value = !_readOnly.value
    }
    private val iconEdit=Icons.Filled.Edit
    private val iconSave=Icons.Filled.Save
    private val _floatingActionButtonIcon = MutableStateFlow(iconEdit)
    val floatingActionButtonIcon = _floatingActionButtonIcon.asStateFlow()

    fun toogleFlootingActionButtonIcon(){
        when(_floatingActionButtonIcon.value){
            iconEdit -> _floatingActionButtonIcon.value = iconSave
            iconSave -> {
                saveNote()
                _floatingActionButtonIcon.value = iconEdit
            }
        }
    }

    private fun saveNote() {

    }


}

