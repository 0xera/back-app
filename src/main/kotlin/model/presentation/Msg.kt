package model.presentation

import database.Author
import database.Repo
import model.domain.AuthorDto

sealed class Msg {
    data class SelectedDirectory(val path: String) : Msg()
    object Rollback : Msg()
    data class Reset(val hard: Boolean = false) : Msg()
    data class Command(val path: String, val commandVal: String, val author: Author?, val repo: Repo) : Msg()
    data class CreatedAuthor(val authorDto: AuthorDto) : Msg()
    object CreateAuthor : Msg()
    object StartChoosing : Msg()
    object Init : Msg()
    object CancelCreateConfig : Msg()
    object Any : Msg()
    data class ConfirmCreateConfig(val path: String) : Msg()
}
