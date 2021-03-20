package domain.usecases

import database.Author
import database.Repo
import domain.Repository
import model.domain.ShowDto

class ResetUseCase(private val repository: Repository, private val configureUseCase: ConfigureUseCase) {

    suspend fun reset(path: String, author: Author, repo: Repo, arguments: Map<String, String>): ShowDto? =
        if (configureUseCase.handleSelectDirectory(path) == null) {
            configureUseCase.createConfigFile(path)
            null
        } else {
            repository.reset(path, author, repo, arguments)
        }
}