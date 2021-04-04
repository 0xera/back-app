package data

import com.example.database.git.BackupDatabase
import data.ext.findFile
import data.ext.readLongFirstLine
import database.*
import domain.Repository
import model.domain.AuthorDto
import model.domain.ShowDto
import model.domain.Status
import utils.Date
import java.io.File

class RepositoryImpl(private val backupDatabase: BackupDatabase) : Repository {

    override suspend fun getConfigFile(path: String) = File(path).findFile(configName)

    override suspend fun createConfigFile(path: String): File {
        val file = File(path, configName)
        val repoId = backupDatabase.repoQueries.run {
            insert(file.parentFile.name)
            lastInsertRowId().executeAsOne()
        }

        file.apply {
            writeText(repoId.toString())
            setReadable(true)
            setWritable(false)
        }
        return file
    }

    override suspend fun createAuthor(authorDto: AuthorDto): List<Author> {
        return backupDatabase.authorQueries.run {
            insert(authorDto.name, authorDto.email)
            getAll().executeAsList()
        }
    }

    override suspend fun createFix(
        path: String,
        author: Author,
        repo: Repo,
        arguments: Map<String, String>,
    ): ShowDto {
        val listFiles = getFilesInPath(path) ?: emptyArray()


        if (arguments.containsKey("amend")) {
            backupDatabase.fixQueries.deleteLastest(repo.id)
        }

        val lastFiles = getLastFixFiles(repo)

        val fixId = backupDatabase.run {
            fixQueries.insert(arguments.getOrDefault("name", "no name"), Date(), author.id, repo.id)
            fixQueries.lastInsertRowId().executeAsOne()

        }


        for (file in listFiles) {
            backupDatabase.run {
                val fileStatus = getFileStatus(file, lastFiles)
                fileQueries.insert(file.name, fileStatus, file.readBytes(), file.length(), repo.id)
                val fileId = fileQueries.lastInsertRowId().executeAsOne()
                fileToFixQueries.insert(fixId, fileId)
            }
        }

        val deletedFiles = lastFiles.filter { file ->
            file.name !in listFiles.map { it.name }
                    && !checkContent(file.data, listFiles)
                    && file.status != Status.Deleted
        }

        for (file in deletedFiles) {
            backupDatabase.run {
                fileQueries.insert(file.name, Status.Deleted, null, file.fileSize, repo.id)
                val fileId = fileQueries.lastInsertRowId().executeAsOne()
                fileToFixQueries.insert(fixId, fileId)
            }
        }


        return backupDatabase.run {
            val fix = fixQueries.getLatest(repo.id).executeAsOne()
            val isNotChanged = fileToFixQueries.getByFix(fix.id).executeAsList()
                .map { fileQueries.get(it.fileId).executeAsOne() }
                .all { fileData -> fileData.status == Status.NotChanged }

            if (isNotChanged) {
                fixQueries.delete(fix.id)
            }

            getShowDto(repo, path)
        }
    }

    private fun getLastFixFiles(repo: Repo): List<FileData> {
        val lastFix = backupDatabase.fixQueries.getLatest(repo.id).executeAsOneOrNull()

        return if (lastFix != null) backupDatabase.fileToFixQueries.getByFix(lastFix.id).executeAsList()
            .map { backupDatabase.fileQueries.get(it.fileId).executeAsOne() }
        else emptyList()
    }

    private fun getFilesInPath(path: String) = File(path).listFiles { file ->
        !file.isDirectory && file.name != configName
    }

    private fun checkContent(bytes: ByteArray?, listFiles: Array<File>): Boolean {
        val first: ByteArray? = listFiles.map { it.readBytes() }
            .firstOrNull { it.contentEquals(bytes) }
        return if (first == null) return false else true
    }

    private fun getFileStatus(file: File, lastFiles: List<FileData>): Status {
        val firstFile = lastFiles.firstOrNull { it.name == file.name }
        return if (firstFile != null) {
            if (firstFile.fileSize == file.length()) Status.NotChanged
            if (checkContent(file.readBytes(), lastFiles)) Status.NotChanged else Status.Modified
        } else {
            if (checkContent(file.readBytes(), lastFiles)) Status.Renamed else Status.Created
        }
    }

    private fun checkContent(bytes: ByteArray?, lastFiles: List<FileData>): Boolean {
        val first: ByteArray? = lastFiles.map { it.data }
            .firstOrNull { it.contentEquals(bytes) }
        return if (first == null) return false else true
    }


    private fun getFixToFiles(repoId: Long): LinkedHashMap<Fix, List<FileData>> {
        val grouped: Map<Long, List<FileToFix>> = backupDatabase.fileToFixQueries.getAll().executeAsList()
            .groupBy { fileToFix -> fileToFix.fixId }

        val map = LinkedHashMap<Fix, List<FileData>>()
        for ((fixId, listFile) in grouped) {
            val fix = backupDatabase.fixQueries.get(fixId).executeAsOne()
            if (fix.repoId != repoId) continue

            val fileDataList = listFile.map { fileToFix ->
                backupDatabase.fileQueries.get(fileToFix.fileId).executeAsOne()
            }
            map[fix] = fileDataList
        }
        return map
    }

    override suspend fun readConfigFile(configFile: File): ShowDto {
        val repoId = configFile.readLongFirstLine()

        return backupDatabase.run {
            getShowDto(repoQueries.get(repoId).executeAsOne(), configFile.parent)
        }
    }

    override suspend fun rollback(path: String, author: Author, repo: Repo): ShowDto {
        getFilesInPath(path)
            ?.onEach { file -> file.delete() }

        val lastFixFiles = getLastFixFiles(repo)
        for (fileData in lastFixFiles) {
            if (fileData.data != null && fileData.status != Status.Deleted)
                File(path, fileData.name).writeBytes(fileData.data)
        }

        return getShowDto(repo, path)

    }

    override suspend fun reset(path: String, author: Author, repo: Repo, arguments: Map<String, String>): ShowDto {
        backupDatabase.fixQueries.deleteLastest(repo.id)
        if (arguments.containsKey("hard")) {
            rollback(path, author, repo)
        }
        return getShowDto(repo, path)
    }

    private fun getShowDto(repo: Repo, path: String): ShowDto {
        val map = getFixToFiles(repo.id)
        return ShowDto(
            path = path,
            repo = repo,
            authors = backupDatabase.authorQueries.getAll().executeAsList(),
            fixToFiles = map
        )
    }
}
