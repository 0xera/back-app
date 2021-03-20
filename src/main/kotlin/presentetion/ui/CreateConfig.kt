package presentetion.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.presentation.State

@Composable
fun CreateConfigScreen(
    state: State.CreatingConfig,
    onCreateConfig: (String) -> Unit,
    onCancelCreateConfig: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create config file?")
        Row {
            Button(
                modifier = Modifier.padding(4.dp),
                onClick = { onCreateConfig.invoke(state.path)}
            ) {
                Text("Create")
            }
        }

        Button(
            modifier = Modifier.padding(4.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.secondary,
            ),
            onClick = { onCancelCreateConfig.invoke() }
        ) {
            Text(color = MaterialTheme.colors.onSecondary, text = "Cancel")
        }
    }

}