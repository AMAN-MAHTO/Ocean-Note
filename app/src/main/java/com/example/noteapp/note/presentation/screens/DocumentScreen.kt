package com.example.noteapp.note.presentation.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.example.noteapp.Permission
import com.example.noteapp.R
import com.example.noteapp.Screen
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.components.CustomFloatingActionButton
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.presentation.view_models.DocumentState
import com.example.noteapp.note.presentation.view_models.DocumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@Composable
fun DocumentScreen(
    modifier: Modifier = Modifier,
    viewModel: DocumentViewModel = hiltViewModel(),
    navHostController: NavHostController,

    ) {


    DocumentContent(
        onClickBack = {
            viewModel.onCloseOrSave(navHostController)
        },
        onBackPress = viewModel::onBackPress,
        state = viewModel.state.collectAsState(),
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onClickDelete = viewModel::onClickDelete,
        onDeleteAlertDialogConfirmation = viewModel::onDeleteAlertDialogConfirmation,
        onDeleteAlertDialogDismissRequest = viewModel::onDeleteAlertDialogDismissRequest,
        onClickShare = { navHostController.navigate(Screen.Share.setId(viewModel.docId.value.toString())) },
        onClickManageAccess = { navHostController.navigate(Screen.ManageAccess.setId(viewModel.docId.value.toString())) },
        onDismissShareDialogRequest = viewModel::onDismissShareDialogRequest,
        navHostController = navHostController,
        onClickFAB = viewModel::onClickFAB,
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentContent(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onBackPress: () -> Unit,
    state: State<DocumentState>,
    onTitleChange: (title: String) -> Unit,
    onBodyChange: (title: String) -> Unit,
    onClickDelete: () -> Unit,
    onDeleteAlertDialogDismissRequest: () -> Unit,
    onDeleteAlertDialogConfirmation: (navHostController: NavHostController) -> Unit,
    onClickShare: () -> Unit,
    onClickManageAccess: () -> Unit,
    onDismissShareDialogRequest: () -> Unit,
    navHostController: NavHostController,
    onClickFAB: () -> Unit,

    ) {
    var expanded by remember { mutableStateOf(false) }


    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            TopAppBar(
                title = {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy((-16).dp)
                    ) {
                        for (user in state.value.currentEditors)
                            if (user.profilePic != null) {
                                AsyncImage(
                                    model = user.profilePic,
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {

                                Icon(
                                    painter = painterResource(id = R.drawable.user),
                                    modifier = Modifier.size(18.dp),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = ""
                        )

                    }
                },
                actions = {
                    if (!state.value.isLoading) {
                        if (state.value.permission == Permission.ALL) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More actions"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {

                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickDelete()

                                },
                                    text = {
                                        Text("Delete")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })

                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickShare()
                                }, text = {
                                    Text("Share")
                                },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = "Share",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickManageAccess()
                                }, text = {
                                    Text("Manage Access")
                                },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.People,
                                            contentDescription = "Manage Access",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })
                            }
                        }
                    }
                }
            )

        },
        floatingActionButton = {
            if (state.value.permission != Permission.READ)
                CustomFloatingActionButton(
                    icon = if (!state.value.editMode) Icons.Default.Edit else Icons.Default.Save,
                    onClick = onClickFAB
                )

        }
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner) {
            Log.d("Lifecycle", "DocumentContent: disposable")
            val observer = LifecycleEventObserver { source, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    Log.d("Lifecycle", "DocumentContent: on destroy disposable")

                    onBackPress()
                }
            }
//
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {

                Log.d("Lifecycle", "DocumentContent: on dispose")

                lifecycleOwner.lifecycle.removeObserver(observer)
            }

        }

        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.value.isLoading) {
                DocumentContentLoading()
            } else {
                if (state.value.userData != null) {

                    if (state.value.permission != Permission.READ && state.value.editMode) {

                        DocumentContentWrite(
                            state = state,
                            onTitleChange = onTitleChange,
                            onBodyChange = onBodyChange
                        )
                    } else {

                        DocumentContentRead(doc = state.value.document)
                    }


                }
            }

        }
        when {
            state.value.openDeleteAlertDialog -> {
                DeleteConfirmationDialogBox(
                    onDismissRequest = onDeleteAlertDialogDismissRequest,
                    onConfirmation = { onDeleteAlertDialogConfirmation(navHostController) }
                )


            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentContentRead(modifier: Modifier = Modifier, doc: Document) {
    val colors = if (isSystemInDarkTheme()) {
        listOf(
            Color(0xFF0073A3), // Dark blue gradient start
            Color(0xFF005377), // Dark blue gradient middle
            Color(0xFF003F5C), // Dark blue gradient middle
            Color(0xFF002B3F)
        )
    } else {
        listOf(
            Color(0xFF72B8FF),
            Color(0xFF619FFF),
            Color(0xFF4C8CFF),
            Color(0xFF325899),
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        OutlinedTextField(
            value = doc.title,
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = {
                Text(
                    "Title",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                )
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            onValueChange = { },
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),

            )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            readOnly = true,
            value = doc.body,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    "Body",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onValueChange = { },
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(
                        width = 1.dp,
                        brush = Brush.verticalGradient(colors)
                    ),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),


            )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentContentWrite(
    modifier: Modifier = Modifier,
    state: State<DocumentState>,
    onTitleChange: (title: String) -> Unit,
    onBodyChange: (title: String) -> Unit
) {
    val colors = if (isSystemInDarkTheme()) {
        listOf(
            Color(0xFF0073A3), // Dark blue gradient start
            Color(0xFF005377), // Dark blue gradient middle
            Color(0xFF003F5C), // Dark blue gradient middle
            Color(0xFF002B3F)
        )
    } else {
        listOf(
            Color(0xFF72B8FF),
            Color(0xFF619FFF),
            Color(0xFF4C8CFF),
            Color(0xFF325899),
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        OutlinedTextField(
            value = state.value.document.title,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Title",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                )
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            onValueChange = onTitleChange,
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),

            )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.value.document.body,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    "Body",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onValueChange = onBodyChange,
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(
                        width = 1.dp,
                        brush = Brush.verticalGradient(colors)
                    ),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),


            )

//        TextEditor()
    }
}

@Composable
fun TextEditor() {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { isBold = !isBold }) {
                Text(if (isBold) "Bold ✓" else "Bold")
            }
            TextButton(onClick = { isItalic = !isItalic }) {
                Text(if (isItalic) "Italic ✓" else "Italic")
            }
            TextButton(onClick = { isUnderlined = !isUnderlined }) {
                Text(if (isUnderlined) "Underline ✓" else "Underline")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface)
        ) {

            BasicTextField(
                value = textState,
                onValueChange = { newTextState ->
                    val newText = newTextState.annotatedString
                    val oldText = textState.annotatedString
                    val start = newTextState.selection.start
                    val end = newTextState.selection.end

                    // Build the AnnotatedString with styles applied to the selected text
                    if (newText.text != oldText.text) {
                        if (!(newText.length < oldText.length)) {
                            val annotatedString = buildAnnotatedString {
                                if (start != 0) {
                                    append(oldText.subSequence(TextRange(0, start - 1)))
                                }

                                // Apply styles to the selected text
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                        textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None,
                                        fontSize = 18.sp,
                                        color = Color.Red
                                    )
                                ) {
                                    append(newText.get(start - 1))
                                }
                                if (start - 1 < oldText.length)
                                    append(
                                        oldText.subSequence(
                                            TextRange(
                                                start - 1,
                                                oldText.length
                                            )
                                        )
                                    )
                            }

                            textState = newTextState.copy(annotatedString = annotatedString)
                        } else {
                            textState =
                                newTextState.copy(annotatedString = newTextState.annotatedString)
                        }
                    } else {
                        textState = newTextState.copy(annotatedString = oldText)
                    }
                },
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            )
        }
        Text(
            text = buildAnnotatedString {
                append(textState.annotatedString)
                // Add your own styles and formatting here if needed
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color.Gray)
        )
    }
}

@Composable
fun DocumentContentLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
private fun DocumentContentPrev() {
    val doc = Document(
        id = "",
        title = "",
        body =
        "Google Map\n" +
                "Review\n" +
                "Jwt and Google sign in\n" +
                "\n" +
                "\n" +
                "Filter Heritage sites list, by user current location, showing the nearest locations on top\n" +
                "\n" +
                "\n" +
                " Searching of sites\n" +
                " \n" +
                " \n" +
                " \n" +
                "  \n" +
                " \n" +
                " - [ ] Google Map\n" +
                "- [ ] Review\n" +
                "- [ ] Jwt and Google sign in\n" +
                "- [ ] Filter Heritage sites list, by user current location, showing the nearest locations on top\n" +
                "- [ ]  Searching of sites\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n",
        ownerId = "G55ICUXesqS0co8U10E8s3acl3w2",
        currentEditors = listOf("editor", "editor2"),
        lastEditTime = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    AppTheme {


        DocumentContent(
            onClickBack = { },
            onBackPress = { },
            state = remember {
                mutableStateOf(
                    DocumentState(
                        document = doc,
                        permission = Permission.ALL,
                        editMode = false,
                        isLoading = false,
                        userData = UserData("", "", "", "")
                    )
                )
            },
            onTitleChange = {},
            onBodyChange = {},
            onClickDelete = { /*TODO*/ },
            onDeleteAlertDialogDismissRequest = { /*TODO*/ },
            onDeleteAlertDialogConfirmation = {},
            onClickShare = { /*TODO*/ },
            onClickManageAccess = { /*TODO*/ },
            onDismissShareDialogRequest = { /*TODO*/ },
            navHostController = rememberNavController()
        ) {

        }
    }
}