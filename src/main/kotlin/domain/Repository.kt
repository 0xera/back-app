package domain

import database.Author
import database.Repo
import model.domain.AuthorDto
import model.domain.ShowDto
import java.io.File

interface Repository {
    suspend fun getConfigFile(path: String): File?
    suspend fun createConfigFile(path: String): File
    suspend fun createAuthor(authorDto: AuthorDto): List<Author>
    suspend fun createFix(path: String, author: Author, repo: Repo, arguments: Map<String, String>) : ShowDto
    suspend fun readConfigFile(configFile: File): ShowDto
    suspend fun rollback(path: String, author: Author, repo: Repo): ShowDto
    suspend fun reset(path: String, author: Author, repo: Repo, arguments: Map<String, String>): ShowDto?
}