package com.example.noteapp.screens.note

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Note(
    noteViewModel: NoteViewModel = hiltViewModel(),
    onEditBackIconClickNavigation:()->Unit
) {
    val TAG = "EditNote"
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isEditorInitalTextSet = noteViewModel.isEditorInitalTextSet.collectAsState()
    var note = noteViewModel.note.collectAsState()
    val state = rememberRichTextState()
    if(!isEditorInitalTextSet.value){
        Log.d(TAG, "seting text again: isEditorIntialTextSet ")
        state.setHtml(note.value.data)
//        state.setMarkdown("**Compose** *Rich* Editor")
        noteViewModel.updateIsEditorInitalTextSet(true)
    }

    Log.d(TAG, "rich text editor: ${note.value}  ")
    val titleSize = MaterialTheme.typography.displaySmall.fontSize
    val subtitleSize = MaterialTheme.typography.titleLarge.fontSize



    val openAlertDialog = remember { mutableStateOf(false) }
    val isEdited = noteViewModel.isEdited.collectAsState()

    if(openAlertDialog.value){
        Log.d(TAG, "_openAlertDialog: ${openAlertDialog.value}")

            SaveAlertDialog(
                onDismissRequest = {
                    openAlertDialog.value = false
                    onEditBackIconClickNavigation()},
                onConfirmation = {
                    openAlertDialog.value = false
                    val save = noteViewModel.saveChanges(state.toHtml())
                    Log.d(TAG, "save status: $save")
                    onEditBackIconClickNavigation()

                },

                dialogText = "Want to save the changes?",
                icon = Icons.Default.WarningAmber
            )

    }




    val _topAppBarTitle = remember{ mutableStateOf("mode: reading") }
    val _editIcon = remember {
        mutableStateOf(Icons.Outlined.Edit)
    }
    val readOnly = noteViewModel.readOnly.collectAsState()
    if(!readOnly.value){
        _topAppBarTitle.value = "mode: editing"

        _editIcon.value = Icons.Filled.Edit
    }else{
        _topAppBarTitle.value = "mode: reading"
        _editIcon.value = Icons.Outlined.Edit
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = _topAppBarTitle.value,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if(isEdited.value){
                            openAlertDialog.value = true
                        }else{
                            onEditBackIconClickNavigation()
                        }



                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "")
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = { noteViewModel.onEditIconClick() },

                            ) {

                            Icon(imageVector = _editIcon.value, contentDescription = "")
                        }
                    }


                },

                )
        },

    ) {

        Column(
            Modifier.padding(it)
        ) {
            if(!readOnly.value){
                EditorControls(
                    modifier = Modifier.weight(2f),
                    state = state,
                    onBoldClick = {
                        state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    },
                    onItalicClick = {
                        state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    },
                    onUnderlineClick = {
                        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    },
                    onTitleClick = {
                        state.toggleSpanStyle(SpanStyle(fontSize = titleSize))
                    },
                    onSubtitleClick = {
                        state.toggleSpanStyle(SpanStyle(fontSize = subtitleSize))
                    },
                    onTextColorClick = {
                        state.toggleSpanStyle(SpanStyle(color = Color.Red))
                    },
                    onStartAlignClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Start))
                    },
                    onEndAlignClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.End))
                    },
                    onCenterAlignClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
                    },
                    onExportClick = {
                        noteViewModel.saveChanges(state.toHtml())
                        Log.d(TAG, state.toHtml())
                    }
                )
            }

            RichTextEditor(

                readOnly = readOnly.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f),
                state = state,
            )



        }




    }


}



@Composable
fun SaveAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
//    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        }
//        ,
//        title = {
//            Text(text = dialogTitle)
//        }
        ,
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Don't Save")
            }
        }
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorControls(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onTextColorClick: () -> Unit,
    onStartAlignClick: () -> Unit,
    onEndAlignClick: () -> Unit,
    onCenterAlignClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    var boldSelected = rememberSaveable { mutableStateOf(false) }
    var italicSelected  = rememberSaveable { mutableStateOf(false) }
    var underlineSelected  = rememberSaveable  { mutableStateOf(false) }
    var titleSelected  = rememberSaveable  { mutableStateOf(false) }
    var subtitleSelected  = rememberSaveable  { mutableStateOf(false) }
    var textColorSelected  = rememberSaveable  { mutableStateOf(false) }
    var linkSelected  = rememberSaveable  { mutableStateOf(false) }
    var alignmentSelected  = rememberSaveable  { mutableIntStateOf(0) }

    var showLinkDialog  = remember  { mutableStateOf(false) }

//    AnimatedVisibility(visible = showLinkDialog.value) {
//        LinkDialog(
//            onDismissRequest = {
//                showLinkDialog.value = false
//                linkSelected.value = false
//            },
//            onConfirmation = { linkText, link ->
//                state.addLink(
//                    text = linkText,
//                    url = link
//                )
//                showLinkDialog.value = false
//                linkSelected.value = false
//            }
//        )
//    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlWrapper(
            selected = boldSelected.value,
            onChangeClick = { boldSelected.value = it },
            onClick = onBoldClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = "Bold Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = italicSelected.value,
            onChangeClick = { italicSelected.value = it },
            onClick = onItalicClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = "Italic Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = underlineSelected.value,
            onChangeClick = { underlineSelected.value = it },
            onClick = onUnderlineClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatUnderlined,
                contentDescription = "Underline Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = titleSelected.value,
            onChangeClick = { titleSelected.value = it },
            onClick = onTitleClick
        ) {
            Icon(
                imageVector = Icons.Default.Title,
                contentDescription = "Title Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = subtitleSelected.value,
            onChangeClick = { subtitleSelected.value = it },
            onClick = onSubtitleClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "Subtitle Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = textColorSelected.value,
            onChangeClick = { textColorSelected.value = it },
            onClick = onTextColorClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatColorText,
                contentDescription = "Text Color Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = linkSelected.value,
            onChangeClick = { linkSelected.value = it },
            onClick = { showLinkDialog.value = true }
        ) {
            Icon(
                imageVector = Icons.Default.AddLink,
                contentDescription = "Link Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected.value == 0,
            onChangeClick = { alignmentSelected.value = 0 },
            onClick = onStartAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatAlignLeft,
                contentDescription = "Start Align Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected.value == 1,
            onChangeClick = { alignmentSelected.value = 1 },
            onClick = onCenterAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatAlignCenter,
                contentDescription = "Center Align Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected.value == 2,
            onChangeClick = { alignmentSelected.value = 2 },
            onClick = onEndAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatAlignRight,
                contentDescription = "End Align Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        ControlWrapper(
            selected = true,
            selectedColor = MaterialTheme.colorScheme.tertiary,
            onChangeClick = { },
            onClick = onExportClick
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Export Control",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ControlWrapper(
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.inversePrimary,
    onChangeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(size = 6.dp))
            .clickable {
                onClick()
                onChangeClick(!selected)
            }
            .background(
                if (selected) selectedColor
                else unselectedColor
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(size = 6.dp)
            )
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun NotePreview() {
    val state = rememberRichTextState()
    val value = remember {
        mutableStateOf(state.toMarkdown())
    }
    Column {
        RichTextEditor(state = state)
        Button(onClick = {
            value.value = state.toMarkdown()
            Log.d("TAG", "NotePreview: ${state.toMarkdown()}")
        }) {
            Text(text = value.value)
        }
    }
}



//                TextField(
//
//                    readOnly = readOnly.value,
//                    value = title.value,
//                    textStyle = MaterialTheme.typography.headlineLarge,
//                    modifier = Modifier.fillMaxWidth(1f),
//                    placeholder = { Text(text = "Heading", style = MaterialTheme.typography.headlineLarge)},
//                    onValueChange = {
//
//                        noteViewModel.setTitle(it)
//                    })
//
//                TextField(
//                    readOnly = readOnly.value,
//                    value = body.value,
//                    placeholder = { Text(text = "Body", style = MaterialTheme.typography.bodyMedium)},
//                    modifier = Modifier
//                        .fillMaxHeight(1f)
//                        .fillMaxWidth(1f),
//                    textStyle = MaterialTheme.typography.bodyMedium,
//                    onValueChange = {
//                        noteViewModel.setBody(it)
//                    })
