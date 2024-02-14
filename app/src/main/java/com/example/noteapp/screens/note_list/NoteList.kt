package com.example.noteapp.screens.note_list

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.models.Note
import com.example.noteapp.services.DatabaseClient
import com.example.noteapp.services.impl.FirebaseDatabaseClientImpl


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(
    innerPadding: PaddingValues,
    noteListViewModel: NoteListViewModel = hiltViewModel(),
    onNoteListTileClick:()->Unit
) {
    val notes = noteListViewModel.note.collectAsState()
    val isDataFetched = noteListViewModel.isDataFetched.collectAsState()

    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(innerPadding),
        columns = StaggeredGridCells.Adaptive(150.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)


    ){

            if(notes.value.isNotEmpty() ){
                Log.d("FIREBASE", "NoteList: ${notes.value}")
                val n = notes.value.size - 1
                for (i in 0..n){
                    item {

                        NoteListTile(notes.value[i]){

                            onNoteListTileClick()
                        }
                    }

                }
            }

        else{
            item {
                Text(text = "Loading..")
            }
        }
    }

}


@Composable
fun NoteListTile(note: Note, onNoteListTileClick:()->Unit) {
    Card(
        onClick = {
            onNoteListTileClick()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(text = note.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "May 22, 2024")
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewNoteList() {

    NoteList(innerPadding = PaddingValues(0.dp)){

    }
    
}

@Preview
@Composable
fun PreviewNoteListTile() {

}