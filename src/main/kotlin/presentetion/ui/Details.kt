package presentetion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import database.FileData
import database.Fix
import model.domain.Status
import presentetion.ext.format
import java.util.*

@Composable
fun Details(modifier: Modifier, fixToFiles: LinkedHashMap<Fix, List<FileData>>) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val selectedFix = mutableStateOf(fixToFiles.keys.firstOrNull())
        val columnModifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
            .border(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colors.primary,
                width = 4.dp
            ).background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colors.background
            ).padding(start = 4.dp, end = 4.dp)


        Column(
            modifier = Modifier.weight(1f)

        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp).height(IntrinsicSize.Min)) {
                Text(text = "Name", modifier = Modifier.weight(2f))
                Divider(modifier = Modifier.width(2.dp).fillMaxHeight())
                Text(modifier = Modifier.weight(1f), text = "Author id")
                Divider(modifier = Modifier.width(2.dp).fillMaxHeight())
                Text(text = "Date", modifier = Modifier.weight(2f))

            }

            LazyColumn(modifier = columnModifier) {
                items(items = fixToFiles.keys.toList()) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                            .clickable { selectedFix.value = item }
                            .background(color = if (item.id == selectedFix.value?.id) Color.Blue.copy(alpha = 0.2f)
                            else Color.Transparent),
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        Text(text = item.name, modifier = Modifier.weight(2f))
                        Divider(modifier = Modifier.width(2.dp).fillMaxHeight())
                        Text(modifier = Modifier.weight(1f), text = item.authorId.toString())
                        Divider(modifier = Modifier.width(2.dp).fillMaxHeight())
                        Text(text = item.date.format(), modifier = Modifier.weight(2f))
                    }
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f)

        ) {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = "Changes")
            LazyColumn(modifier = columnModifier) {
                items(items = fixToFiles[selectedFix.value] ?: emptyList()) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        val color = when (item.status) {
                            Status.Created -> Color.Green
                            Status.Modified -> Color.Blue
                            Status.Deleted -> Color.Red
                            Status.Renamed -> Color(0xFFFFA500)
                            else -> Color.DarkGray
                        }
                        if (item.status != Status.NotChanged)
                            Text(text = item.name, color = color)
                    }
                }
            }
        }
    }

}


