package model.presentation

import database.Author
import database.FileData
import database.Fix
import database.Repo

sealed class State {
    object Init : State()
    object Error : State()
    object Loading : State()
    data class CreatingConfig(val path: String) : State()
    data class Show(
        val path: String,
        val repo: Repo,
        val fixToFiles: LinkedHashMap<Fix, List<FileData>>,
        val authors: List<Author>,
    ) : State()
}