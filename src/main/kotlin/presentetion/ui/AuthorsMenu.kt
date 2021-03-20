package presentetion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import database.Author

@Composable
fun AuthorsMenu(
    users: List<Author>,
    selectedAuthor: MutableState<Author?>,
    selectAction: (Author) -> Unit,
    createAuthor: () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Button(onClick = { expanded.value = true },
        modifier = Modifier
            .padding(4.dp)
            .padding(top = 0.dp)
            .fillMaxWidth()) {
        Text("Author: ${selectedAuthor.value?.nickname}")
    }

    DropdownMenu(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
    ) {
        users.forEachIndexed { _, author ->
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    selectAction.invoke(author)
                    expanded.value = false
                }) {
                Text(text = author.nickname)
            }
            Divider()
        }

        Icon(imageVector = Icons.Filled.AddCircle,
            contentDescription = "",
            modifier = Modifier.fillMaxWidth().clickable(onClick = {
                createAuthor.invoke()
                expanded.value = false
            }).padding(4.dp).padding(top = 8.dp))


    }

}
