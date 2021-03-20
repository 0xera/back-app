package presentetion.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.shortcuts
import androidx.compose.ui.unit.dp
import database.Author
import model.presentation.Msg
import model.presentation.State
import presentetion.Store

@ExperimentalAnimationApi
@Composable
fun ShowScreen(
    state: State.Show,
    store: Store,
) {
    val selectedAuthor = remember { mutableStateOf<Author?>(null) }

    if (state.authors.isNotEmpty() && selectedAuthor.value == null) {
        selectedAuthor.value = state.authors.last()
    }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
    ) {
        Row(
            modifier = Modifier.weight(weight = 1f).fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            AuthorsMenu(state.authors, selectedAuthor,
                selectAction = { author -> selectedAuthor.value = author },
                createAuthor = { store.send(Msg.CreateAuthor) }
            )
        }

        Text(text = state.repo.name,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .weight(weight = 1.5f, fill = true)
                .padding(top = 4.dp))

        Details(Modifier.fillMaxWidth().weight(weight = 5f, fill = true), state.fixToFiles)

        Row(
            modifier = Modifier.fillMaxWidth().weight(weight = 2f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom) {
            val textState: MutableState<String> = remember { mutableStateOf("") }

            CommandsInputField(
                textState,
                Modifier.fillMaxWidth().shortcuts {
                    on(Key.Enter) {
                        store.send(Msg.Command(state.path, textState.value, selectedAuthor.value, state.repo))
                        textState.value = ""
                    }
                },
            )

        }
    }
}



