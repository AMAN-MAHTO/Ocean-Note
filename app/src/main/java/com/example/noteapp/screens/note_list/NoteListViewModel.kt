package com.example.noteapp.screens.note_list

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.models.Note
import com.example.noteapp.models.NoteData
import com.example.noteapp.services.DatabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val _notesBulking = mutableListOf(
    NoteData(
        title = "Meeting Notes and Action Items",
        body = "Discussed project updates and deadlines with the team. Assigned action items to each team member.",
        createdDate = "2024-01-10 09:30:00",
        updatedDate = "2024-01-10 09:45:00"
    ),
    NoteData(
        title = "Grocery Shopping List",
        body = "Need to buy milk, eggs, bread, and vegetables for the week.",
        createdDate = "2024-01-11 10:00:00",
        updatedDate = "2024-01-11 10:15:00"
    ),
    NoteData(
        title = "Researching Travel Plans for Summer Vacation",
        body = "Researching flights and accommodations for a week-long vacation in Europe.",
        createdDate = "2024-01-12 11:30:00",
        updatedDate = "2024-01-12 12:00:00"
    ),
    NoteData(
        title = "Project Proposal Draft",
        body = "Preparing the draft for the upcoming project proposal.",
        createdDate = "2024-01-13 14:00:00",
        updatedDate = "2024-01-13 15:00:00"
    ),
    NoteData(
        title = "Fitness Goals and Progress",
        body = "Tracking daily workouts and progress towards fitness goals.",
        createdDate = "2024-01-14 16:00:00",
        updatedDate = "2024-01-14 17:00:00"
    ),
    NoteData(
        title = "Recipe Ideas",
        body = "Collecting recipes for upcoming meals and dinner parties.",
        createdDate = "2024-01-15 18:00:00",
        updatedDate = "2024-01-15 18:30:00"
    ),
    NoteData(
        title = "Book Recommendations",
        body = "List of books to read based on recommendations from friends and colleagues.",
        createdDate = "2024-01-16 20:00:00",
        updatedDate = "2024-01-16 20:45:00"
    ),
    NoteData(
        title = "Home Improvement Plans",
        body = "Planning renovations and upgrades for the home.",
        createdDate = "2024-01-17 21:30:00",
        updatedDate = "2024-01-17 22:15:00"
    ),
    NoteData(
        title = "Career Development Goals",
        body = "Setting goals and action plans for career growth and advancement.",
        createdDate = "2024-01-18 08:00:00",
        updatedDate = "2024-01-18 09:00:00"
    ),
    NoteData(
        title = "Language Learning Progress",
        body = "Tracking progress in learning a new language through daily practice and exercises.",
        createdDate = "2024-01-19 09:30:00",
        updatedDate = "2024-01-19 10:30:00"
    )
)
@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class NoteListViewModel @Inject constructor(private val databaseClient: DatabaseClient) :ViewModel() {

    private val _notes = MutableStateFlow(listOf(Note("",NoteData("","","",""))))
    val note = _notes.asStateFlow()
    private val _isDataFetched = MutableStateFlow(false)
    val isDataFetched =_isDataFetched.asStateFlow()


    init {
        viewModelScope.launch {

//            _notesBulking.forEach {
//                databaseClient.addNote(it)
//            }

            // fetching all the notes of user
            _notes.value = databaseClient.getNotes()
            Log.d("FIREBASE", "NoteList view-model init: ${_notes.value}")
            _isDataFetched.value = true

        }
    }



    fun setSelectedNote(noteDataType: NoteData) {
//        _selectedNote.value = noteDataType
    }



    fun updateNote(note: NoteData){
//        _notes.value.find { it.id == note.id }.apply {
//            this?.title = note.title
//            this?.body = note.body
//            this?.updatedDate = "Wed 14, 2024"
//        }
        Log.d("NOTES", "updateNote: ${_notes.value[0]}")
    }


}
