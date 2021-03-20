import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
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

                is State.CreatingConfig -> CreateConfigScreen(
                    state as State.CreatingConfig,
                    { path -> store.send(Msg.ConfirmCreateConfig(path)) },
                    { store.send(Msg.CancelCreateConfig) })

                is State.Loading -> LoadingScreen()

                is State.Show -> ShowScreen(state as State.Show, store)

                else -> Box { Text("error") }

            }

            if (showAuthorCreateFrom.value) {
                UserCreateScreen(showAuthorCreateFrom, store)

            }

        }
    }
}

@Composable
fun UserCreateScreen(
    showAuthorCreateFrom: MutableState<Boolean>,
    store: Store,
) {
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Create User")
        OutlinedTextField(
            label = { Text("Name") },
            value = nameState.value,
            onValueChange = { nameState.value = it })
        OutlinedTextField(
            label = { Text("Email") },
            value = emailState.value,
            onValueChange = { emailState.value = it })
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
            onClick = {
                if (nameState.value.isNotBlank() && emailState.value.isNotBlank()) {
                    showAuthorCreateFrom.value = false
                    store.send(Msg.CreatedAuthor(AuthorDto(nameState.value, emailState.value)))
                }

            }) {
            Text("Confirm")
        }
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            onClick = {
                showAuthorCreateFrom.value = false
                store.send(Msg.Any)
            }) {
            Text("Dismiss")
        }
    }
}
