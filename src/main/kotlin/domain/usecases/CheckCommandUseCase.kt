package domain.usecases

import model.domain.CommandDto
import model.domain.CommandDto.Command
import java.util.*
import kotlin.collections.HashMap

class CheckCommandUseCase {

    fun checkCommand(input: String): CommandDto {

        val split = input.trim().split(" ", limit = 2)

        if (split.isNullOrEmpty()) return CommandDto(command = Command.Unknown)

        if (split.size == 1) return CommandDto(
            command = processCommand(split.first())
        )

        return CommandDto(
            command = processCommand(split.first()),
            arguments = processArguments(split.last())
        )


    }

    private fun processArguments(input: String): Map<String, String> {
        var i = 0
        val argMap = HashMap<String, String>()
        while (i < input.length) {
            if (input[i] == '-') {
                i++
                if (i >= input.length) break
                val argKey = buildString {
                    while ( i < input.length && input[i] != ' ') append(input[i++])
                }
                if (i >= input.length) {
                    argMap[argKey] = ""
                    break
                }

                while (i < input.length && input[i] == ' ') i++
                if (i >= input.length) break

                if(input[i] == '-') {
                    argMap[argKey] = ""
                    continue
                }

                val closure = if (input[i] == '"') {
                    i++
                    '"'
                } else {
                    ' '
                }
                if (i >= input.length) break
                val argValue = buildString {
                    while ( i < input.length && input[i] != closure) append(input[i++])
                }
                argMap[argKey] = argValue
            } else {
                i++
            }
        }
        return argMap
    }

    private fun processCommand(input: String) = when (input.toLowerCase(Locale.US)) {
        "fix" -> Command.Fix
        "rollback" -> Command.Rollback
        "reset" -> Command.Reset
        "restart" -> Command.Restart
        else -> Command.Unknown
    }

}