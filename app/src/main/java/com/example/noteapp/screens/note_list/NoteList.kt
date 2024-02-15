package com.example.noteapp.screens.note_list

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.models.Note


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(
    innerPadding: PaddingValues,
    noteListViewModel: NoteListViewModel = hiltViewModel(),
    onNoteListTileClick: (id:String) -> Unit
) {
    val notes = noteListViewModel.note.collectAsState()
    val isDataFetched = noteListViewModel.isDataFetched.collectAsState()


    if (isDataFetched.value) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(innerPadding),
            columns = StaggeredGridCells.Adaptive(150.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp)

        ) {
            if (notes.value.isNotEmpty()) {
                Log.d("FIREBASE", "NoteList tile: ${notes.value}")
                val n = notes.value.size - 1
                for (i in 0..n) {
                    item {

                        NoteListTile(notes.value[i]) {

                            onNoteListTileClick(it)
                        }
                    }

                }
            }

        }
    } else {
        Box(
            Modifier.fillMaxSize(1f),
            contentAlignment = Alignment.TopStart
        ) {

            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
        }

    }
}


@Composable
fun NoteListTile(note: Note, onNoteListTileClick: (id: String) -> Unit) {
    Card(
        onClick = {
            onNoteListTileClick(note.id)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(text = note.data.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "May 22, 2024")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewNoteList() {

    Box(
        Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.TopStart
    ) {

        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
    }


}

@Preview
@Composable
fun PreviewNoteListTile() {

}