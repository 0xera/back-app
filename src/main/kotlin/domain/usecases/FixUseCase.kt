package domain.usecases

import database.Author
import database.Repo
import domain.Repository
import model.domain.ShowDto

class FixUseCase(private val repository: Repository, private val configureUseCase: ConfigureUseCase) {

    suspend fun createFix(path: String, author: Author, repo: Repo, arguments: Map<String, String>): ShowDto {
        if (configureUseCase.handleSelectDirectory(path) == null) {
            configureUseCase.createConfigFile(path)
        }
        return repository.createFix(path, author, repo, arguments)
    }

}