package utils;

import com.squareup.sqldelight.ColumnAdapter
import java.util.*

typealias Date = GregorianCalendar

class DateAdapter : ColumnAdapter<Date, Long> {
    override fun encode(value: Date) = value.timeInMillis
    override fun decode(databaseValue: Long) = Date.getInstance().apply {
        timeInMillis = databaseValue
    } as Date
}