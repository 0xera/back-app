package presentetion.ext

import java.text.SimpleDateFormat
import java.util.*

private val simpleDateFormat = SimpleDateFormat("dd-MM-yy hh:mm:ss")

fun GregorianCalendar.format(): String = simpleDateFormat.format(time)
