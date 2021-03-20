package model.domain

data class CommandDto(
    val command: Command,
    val arguments: Map<String, String> = emptyMap(),

    ) {
    enum class Command {
        Fix,
        Rollback,
        Reset,
        Restart,
        Unknown
    }
}