package utils

import com.squareup.sqldelight.ColumnAdapter
import model.domain.Status

class StatusAdapter : ColumnAdapter<Status, String> {
    override fun encode(value: Status) = value.name
    override fun decode(databaseValue: String) = when (databaseValue) {
        "Created" -> Status.Created
        "Modified" -> Status.Modified
        "Deleted" -> Status.Deleted
        "Renamed" -> Status.Renamed
        "NotChanged" -> Status.NotChanged
        else -> Status.Unknown
    }
}