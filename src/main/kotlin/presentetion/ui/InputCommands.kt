package presentetion.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommandsInputField(
    textState: MutableState<String>,
    modifier: Modifier,
) {
    TextField(modifier = modifier
        .padding(2.dp)
        .padding(bottom = 8.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colors.primary,
            shape = RoundedCornerShape(8.dp)
        ).clip(RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black),
        maxLines = 1,
        singleLine = true,
        value = textState.value,
        onValueChange = { textState.value = it })

}


