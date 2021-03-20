package domain.usecases

import database.Author
import database.Repo
import domain.Repository
import model.domain.ShowDto

class RollbackUseCase(private val repository: Repository, private val configureUseCase: ConfigureUseCase) {
    suspend fun makeRollback(path: String, author: Author, repo: Repo): ShowDto? =
        if (configureUseCase.handleSelectDirectory(path) == null) {
            configureUseCase.createConfigFile(path)
            null
        } else {
            repository.rollback(path, author, repo)
        }
}