package com.example.noteapp.screens.note

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.EDIT_NOTE_ARGUMENT_ID
import com.example.noteapp.models.NoteData
import com.example.noteapp.services.DatabaseClient
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    // as navcontroller is creating this viewmodel, we use it's NavBackStackEntry to excess argument passed during navigation to note_screen
    savedStateHandle: SavedStateHandle,
    private  val databaseClient: DatabaseClient
):ViewModel() {
    private val TAG = "EditNote"
    private val _readOnly = MutableStateFlow(true)
    val readOnly = _readOnly.asStateFlow()

    private val _noteId = MutableStateFlow(savedStateHandle.get<String>(EDIT_NOTE_ARGUMENT_ID))

    private val _note = MutableStateFlow(NoteData())
    private val _title = MutableStateFlow(_note.value.title)
    private val _body = MutableStateFlow(_note.value.body)
    private val _isEdited = MutableStateFlow(false)
    private val _isEditorInitalTextSet = MutableStateFlow(false)
    val isEdited = _isEdited.asStateFlow()
    var title = _title.asStateFlow()
    var body = _body.asStateFlow()
    var isEditorInitalTextSet = _isEditorInitalTextSet.asStateFlow()
    fun setBody(value:String){
        _body.value = value
        _isEdited.value = true
    }
    fun setTitle(value:String){
        _title.value = value
        _isEdited.value = true
    }
    init {
        viewModelScope.launch {
            val id = _noteId.value
            Log.d(TAG, "Note id: $id")
            if(id?.isNotEmpty() == true){

                // fetching note from firestore, it it is null, note value is default NoteData
                _note.value = databaseClient.getNoteById(id)?: NoteData()
                _title.value = _note.value.title
                _body.value = _note.value.body
                Log.d(TAG, "Note value: ${_note.value}")
            }
        }
    }


    fun toogleReadOnly(){
        _readOnly.value = !_readOnly.value
    }
    private val iconEdit=Icons.Filled.Edit
    private val iconSave=Icons.Filled.Save
    private val _floatingActionButtonIcon = MutableStateFlow(iconSave)
    val floatingActionButtonIcon = _floatingActionButtonIcon.asStateFlow()

    fun toogleFlootingActionButtonIcon(){
        when(_floatingActionButtonIcon.value){
            iconEdit -> _floatingActionButtonIcon.value = iconSave
            iconSave -> {

                _floatingActionButtonIcon.value = iconEdit
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveChanges(): Boolean {
        var sucess = false
        val noteid = _noteId.value
        if(noteid != null){
            if(noteid.isEmpty()){
                viewModelScope.launch {
                    val note = _note.value.copy(
                        title = _title.value,
                        body = _body.value,
                        createdDate = LocalDateTime.now().toString(),
                        updatedDate = LocalDateTime.now().toString(),
                    )
                    _noteId.value = databaseClient.addNote(note)
                }
            }else{
                viewModelScope.launch {
                    val note = _note.value.copy(
                        title = _title.value,
                        body = _body.value,
                        updatedDate = LocalDateTime.now().toString(),
                    )
                    sucess = databaseClient.updateNoteById(noteid,note)
                }
            }
        }
        return sucess
    }

    fun updateNote(copy: NoteData) {
        _note.value = copy
    }


    fun onEditIconClick() {
        toogleReadOnly()
    }

    fun updateIsEditorInitalTextSet(value: Boolean) {
        _isEditorInitalTextSet.value = value
    }


}

