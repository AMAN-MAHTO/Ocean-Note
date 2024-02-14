package com.example.noteapp.screens.note

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.models.Note
import com.example.noteapp.screens.note_list.NoteListViewModel




@Composable
fun Note(
    noteViewModel: NoteViewModel = hiltViewModel(),
    noteListViewModel: NoteListViewModel = hiltViewModel()
) {
    val note = Note(
        id = 1,
        title = "Meeting Notes and Action Items",
        body = "Discussed project updates and deadlines with the team. Assigned action items to each team member.",
        createdDate = "2024-01-10 09:30:00",
        updatedDate = "2024-01-10 09:45:00"
    )
    val title = mutableStateOf(note.title)
    val body = mutableStateOf(note.body)
    val readOnly = noteViewModel.readOnly.collectAsState()
    val floatingActionButtonIcon = noteViewModel.floatingActionButtonIcon.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

//                        noteListViewModel.updateNote(
//                            Note(note.value.id,
//                                title.value,body.value,
//                                note.value.createdDate,
//                                note.value.updatedDate)
//                        )

                    noteViewModel.toogleFlootingActionButtonIcon()
                    noteViewModel.toogleReadOnly()
                          },

            ) {
                Icon(imageVector = floatingActionButtonIcon.value, contentDescription = "")
            }
        }
    ) {

        Column(
            Modifier.padding(it)
        ) {
            
            TextField(
                readOnly = readOnly.value,
                value = title.value,
                textStyle = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(1f),
                onValueChange = {

                title.value = it
            })

            TextField(
                readOnly = readOnly.value,
                value = body.value,
                modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = {
                body.value = it
            })


        }
    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun NotePreview() {
    Note()
}


//    val bodyState = rememberRichTextState()
//    val headerState = rememberRichTextState()
//    bodyState.setText(note.body)
//    headerState.setText(note.title)

//            RichTextEditor(state = headerState,
//                readOnly = readOnly.value,
//                textStyle = MaterialTheme.typography.headlineLarge,
//                contentPadding = PaddingValues(bottom = 8.dp),
//                colors = RichTextEditorDefaults.richTextEditorColors(
//                    containerColor = MaterialTheme.colorScheme.surface
//                ))
//
//
//            RichTextEditor(state = bodyState,
//                Modifier.fillMaxHeight(1f),
//                readOnly = readOnly.value,
//                textStyle = MaterialTheme.typography.bodyMedium,
//                contentPadding = PaddingValues(top = 15.dp,  bottom = 15.dp),
//                colors = RichTextEditorDefaults.richTextEditorColors(
//                    containerColor = MaterialTheme.colorScheme.surface
//                ))
//        }