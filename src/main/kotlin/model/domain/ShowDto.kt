package model.domain

import database.Author
import database.FileData
import database.Fix
import database.Repo

data class ShowDto(
    val path: String,
    val repo: Repo,
    val fixToFiles: LinkedHashMap<Fix, List<FileData>>,
    val authors: List<Author>,
)