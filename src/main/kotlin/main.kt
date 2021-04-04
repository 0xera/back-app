
import androidx.compose.animation.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import di.di
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.domain.AuthorDto
import model.presentation.Msg
import model.presentation.State
import model.presentation.UiEvent
import org.kodein.di.instance
import presentetion.Store
import presentetion.ui.CreateConfigScreen
import presentetion.ui.LoadingScreen
import presentetion.ui.ShowScreen
import javax.swing.JFileChooser
import javax.swing.JFileChooser.APPROVE_OPTION
import javax.swing.JFileChooser.DIRECTORIES_ONLY


val store: Store by di.instance()

@ExperimentalAnimationApi
fun main() = Window(
    title = "BackApp", centered = true,
    resizable = false,
    size = IntSize(800, 500)
) {
    MaterialTheme {
        val state by store.state.collectAsState(State.Init)
        val showAuthorCreateFrom = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            MainScope().launch {
                store.uiEvent.collect {
                    when (it) {
                        is UiEvent.ShowFileManager -> {
                            JFileChooser(System.getProperty("user.home")).apply {
                                isMultiSelectionEnabled = false
                                fileSelectionMode = DIRECTORIES_ONLY
                                isFileHidingEnabled = false
                                val buttonRes = showOpenDialog(null)
                                if (selectedFile != null && selectedFile.isDirectory && buttonRes == APPROVE_OPTION) {
                                    store.send(Msg.SelectedDirectory(selectedFile.absolutePath))
                                } else {
                                    store.send(Msg.Init)
                                }
                            }
                        }
                        UiEvent.CreateAuthorDialog -> {
                            showAuthorCreateFrom.value = true
                        }
                    }
                }
            }
        }


        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            when (state) {
                is State.Init -> InitScreen(store)

                is State.CreatingConfig -> ScreenAnimation {
                    CreateConfigScreen(
                        state as State.CreatingConfig,
                        { path -> store.send(Msg.ConfirmCreateConfig(path)) },
                        { store.send(Msg.CancelCreateConfig) })
                }

                is State.Loading -> ScreenAnimation { LoadingScreen() }

                is State.Show -> ScreenAnimation { ShowScreen(state as State.Show, store) }

                else -> Box { Text("error") }

            }
            ScreenAnimation(showAuthorCreateFrom.value) {
                UserCreateScreen(showAuthorCreateFrom, store)
            }

        }

    }
}


@ExperimentalAnimationApi
@Composable
fun ScreenAnimation(visible: Boolean = true, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + expandHorizontally(
            expandFrom = Alignment.Start,
        ),
        exit = slideOutVertically() + shrinkHorizontally(shrinkTowards = Alignment.Start),
        initiallyVisible = false,
        content = content
    )
}

@Composable
fun UserCreateScreen(
    showAuthorCreateFrom: MutableState<Boolean>,
    store: Store,
) {
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            label = { Text("Name") },
            value = nameState.value,
            onValueChange = { nameState.value = it })
        OutlinedTextField(
            label = { Text("Email") },
            value = emailState.value,
            onValueChange = { emailState.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email))
        Row {
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    if (nameState.value.isNotBlank() && emailState.value.isNotBlank()) {
                        showAuthorCreateFrom.value = false
                        store.send(Msg.CreatedAuthor(AuthorDto(nameState.value, emailState.value)))
                    }

                }) {
                Text("Confirm")
            }
            OutlinedButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    showAuthorCreateFrom.value = false
                }) {
                Text(color = MaterialTheme.colors.primary, text = "Dismiss")
            }
        }
    }
}
