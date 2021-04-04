package domain.usecases

import domain.Repository
import model.domain.AuthorDto

class AuthorUseCase(private val repository: Repository) {

    suspend fun create(authorDto: AuthorDto) = repository.createAuthor(authorDto)
}