package model.presentation

import database.Author
import database.Repo
import model.domain.AuthorDto
import java.io.File

sealed class SideEffect {
    data class SelectDirectory(val path: String) : SideEffect()
    data class ReadConfigFile(val configFile: File) : SideEffect()
    data class CreateConfigFile(val path: String) : SideEffect()
    data class Fix(
        val path: String,
        val author: Author,
        val repo: Repo,
        val arguments: Map<String, String>,
    ) : SideEffect()

    data class CheckCommand(val path: String, val commandInput: String, val author: Author?, val repo: Repo) :
        SideEffect()

    data class SaveAuthor(val authorDto: AuthorDto) : SideEffect()
    data class Rollback(
        val path: String,
        val author: Author,
        val repo: Repo,
    ) : SideEffect()

    data class Reset(
        val path: String,
        val author: Author,
        val repo: Repo,
        val arguments: Map<String, String>,
    ) : SideEffect()

}
