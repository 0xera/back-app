package presentetion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import database.Author

@Composable
fun AuthorsMenu(
    authors: List<Author>,
    selectedAuthor: MutableState<Author?>,
    selectAction: (Author) -> Unit,
    createAuthor: () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Button(onClick = {
        if (authors.isNullOrEmpty()) {
            createAuthor.invoke()
        } else {
            expanded.value = true
        }
    },
        modifier = Modifier
            .padding(4.dp)
            .padding(top = 0.dp)) {
        Text(
            buildAnnotatedString {
                val prefix = "Author: "
                append(prefix)
                val author = selectedAuthor.value
                append(if (author != null) "name: ${author.nickname} id: ${author.id}" else "Not selected")
                if (selectedAuthor.value?.nickname.isNullOrEmpty()) {
                    addStyle(SpanStyle(color = Color.Red), prefix.lastIndex, length)
                }

            }
        )
    }
    if (!authors.isNullOrEmpty()) {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            authors.forEachIndexed { _, author ->
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        createAuthor.invoke()
                        expanded.value = false
                    }).padding(4.dp).padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "")
            }


        }
    }

}
