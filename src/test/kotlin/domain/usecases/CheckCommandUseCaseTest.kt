package domain.usecases

import com.google.common.truth.Truth
import model.domain.CommandDto
import org.junit.Test

class CheckCommandUseCaseTest {

    private val checkCommandUseCase = CheckCommandUseCase()

    @Test
    fun `when command is fix`() {
        val expected = CommandDto(CommandDto.Command.Fix)

        val actual = checkCommandUseCase.checkCommand("fix")

        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun `when command is fix with args`() {
        val expected = CommandDto(CommandDto.Command.Fix, mapOf("name" to "new", "arg" to "some val"))

        val actual = checkCommandUseCase.checkCommand("fix -name new -arg \"some val\"")

        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun `when command is fix with args with no value`() {
        val expected = CommandDto(CommandDto.Command.Fix, mapOf( "arg" to "", "name" to "new", "arg2" to ""))

        val actual = checkCommandUseCase.checkCommand("fix -arg -name new -arg2 ")

        Truth.assertThat(actual).isEqualTo(expected)

    }
}