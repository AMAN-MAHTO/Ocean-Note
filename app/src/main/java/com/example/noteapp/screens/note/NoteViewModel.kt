package com.example.noteapp.screens.note

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor():ViewModel() {

    private val _readOnly = MutableStateFlow(true)
    val readOnly = _readOnly.asStateFlow()

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
            iconSave -> _floatingActionButtonIcon.value = iconEdit
        }
    }

}

