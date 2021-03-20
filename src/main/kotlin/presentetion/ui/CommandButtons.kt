//package ui
//
//import Store
//import androidx.compose.animation.Crossfade
//import androidx.compose.animation.animateContentSize
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.ObsoleteCoroutinesApi
//
//@ObsoleteCoroutinesApi
//@Composable
//fun CommandButtons(store: Store, modifier: Modifier) {
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.Bottom) {
//        Button(
//            modifier = Modifier.padding(4.dp),
//            onClick = { store.send(Store.Msg.Init) }
//        ) {
//            Text("New")
//        }
//        Box(Modifier.animateContentSize().padding(4.dp)) {
//            val expanded = remember { mutableStateOf(false) }
//            Crossfade(targetState = expanded) { value ->
//                when (value.value) {
//                    false -> Button(
//                        onClick = { expanded.value = true }
//                    ) {
//                        Text("Commit")
//                    }
//                    true -> Column {
//                        Button(
//                            modifier = Modifier.padding(4.dp),
//                            onClick = {
//                                expanded.value = false
////                                store.send(Store.Msg.Commit(name = "", author = effect.author, repoId = effect.repoId))
//                            }
//                        ) {
//                            Text("standard")
//                        }
//                        Button(
//                            modifier = Modifier.padding(4.dp),
//                            onClick = {
//                                expanded.value = false
////                                store.send(Store.Msg.Command(, name = selectedAuthor.value))
//                            }
//                        ) {
//                            Text("amend")
//                        }
//                    }
//                }
//            }
//
//        }
//        Button(
//            modifier = Modifier.padding(4.dp),
//            onClick = { store.send(Store.Msg.Rollback) }
//
//        ) {
//            Text("Rollback")
//        }
//        Box(Modifier.animateContentSize().padding(4.dp)) {
//            val expanded = remember { mutableStateOf(false) }
//            Crossfade(targetState = expanded) { value ->
//                when (value.value) {
//                    false -> Button(
//                        onClick = { expanded.value = true }
//                    ) {
//                        Text("Reset")
//                    }
//                    true -> Column {
//                        Button(
//                            modifier = Modifier.padding(4.dp),
//                            onClick = {
//                                expanded.value = false
//                                store.send(Store.Msg.Reset(hard = true))
//                            }
//                        ) {
//                            Text("hard")
//                        }
//                        Button(
//                            modifier = Modifier.padding(4.dp),
//                            onClick = {
//                                expanded.value = false
//                                store.send(Store.Msg.Reset())
//                            }
//                        ) {
//                            Text("soft")
//                        }
//                    }
//                }
//            }
//
//        }
//    }
//}
//
//
