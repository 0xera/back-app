package data.ext

import java.io.File

fun File.readLongFirstLine() = readLines().first().toLong()

fun File.findFile( filename: String) =
    listFiles { _, name -> name.equals(filename) }?.firstOrNull()