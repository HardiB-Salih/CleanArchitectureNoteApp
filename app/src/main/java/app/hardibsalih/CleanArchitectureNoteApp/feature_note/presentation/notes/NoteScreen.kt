package app.hardibsalih.CleanArchitectureNoteApp.feature_note.presentation.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.presentation.notes.components.NoteItem
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.presentation.notes.components.OrderSection
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.presentation.util.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    notesViewModel: NotesViewModel = hiltViewModel()
) {

    val state = notesViewModel.state.value
    val scope = rememberCoroutineScope()
    // Create a MutableState for showing/hiding the Snackbar
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarVisibleState.value) {
        if (snackbarVisibleState.value) {
            // Show the snackbar
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = "Note Deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            delay(2000L) // Adjust the duration to match the snackbar display time
            snackbarVisibleState.value = false
        }
    }


    Scaffold(
        floatingActionButtonPosition= FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                          },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Notes")
            }
        },
        snackbarHost = {
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                onDismiss = {
                    snackbarVisibleState.value = false
                    notesViewModel.onEvent(NotesEvent.RestoreNote)
                }
            )
        }

    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                ) {
                Text(text = "Your Notes",
                    style = MaterialTheme.typography.headlineLarge)

                IconButton(onClick = { notesViewModel.onEvent(NotesEvent.ToggleOrderSection) }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    noteOrder = state.noteOrder,
                    onOrderChange = {
                        notesViewModel.onEvent(NotesEvent.Order(it))
                    })
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.notes) {note ->
                    NoteItem(
                        note = note,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}")
                            },
                        onDeleteClick = {
                            notesViewModel.onEvent(NotesEvent.DeleteNote(note))
                            snackbarVisibleState.value = true
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(10.dp),
                action = {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(text = data.visuals.actionLabel ?: "", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            ) {
                Text(
                    text = data.visuals.message,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}