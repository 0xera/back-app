package domain.usecases

import domain.Repository
import java.io.File

class ConfigureUseCase(private val repository: Repository) {

    suspend fun handleSelectDirectory(path: String) = repository.getConfigFile(path)
    suspend fun createConfigFile(path: String) = repository.createConfigFile(path)
    suspend fun readConfigFile(configFile: File) = repository.readConfigFile(configFile)
}