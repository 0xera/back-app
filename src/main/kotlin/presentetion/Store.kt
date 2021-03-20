package presentetion

import domain.usecases.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import model.domain.CommandDto.Command
import model.domain.ShowDto
import model.presentation.Msg
import model.presentation.SideEffect
import model.presentation.State
import model.presentation.UiEvent

class Store(
    private val configureUseCase: ConfigureUseCase,
    private val fixUseCase: FixUseCase,
    private val authorUseCase: AuthorUseCase,
    private val checkCommandUseCase: CheckCommandUseCase,
    private val rollbackUseCase: RollbackUseCase,
    private val resetUseCase: ResetUseCase,
    private val storeScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {


    private val _state = MutableStateFlow<State>(State.Init)
    val state = _state
    private val _uiEvent = MutableSharedFlow<UiEvent>(replay = 0)
    val uiEvent = _uiEvent

    private var actorMsg = Channel<Msg>()
    private var actorEffect = Channel<SideEffect?>()

    init {
        storeScope.launch {
            actorMsg.consumeEach { msg ->
                reduce(msg)
            }
        }

        storeScope.launch {
            actorEffect.consumeEach { effect ->
                handleEffect(effect)
            }
        }

    }

    private suspend fun reduce(msg: Msg) {
        val (state, effect, uiEvent) = when (msg) {
            is Msg.Init -> Triple(State.Init, null, null)
            is Msg.StartChoosing -> Triple(State.Loading, null, UiEvent.ShowFileManager)
            is Msg.CancelCreateConfig -> Triple(State.Error, null, null)
            is Msg.SelectedDirectory -> Triple(State.Loading, SideEffect.SelectDirectory(msg.path), null)
            is Msg.ConfirmCreateConfig -> Triple(State.Loading, SideEffect.CreateConfigFile(msg.path), null)
            is Msg.Command -> Triple(state.value,
                SideEffect.CheckCommand(msg.path, msg.commandVal, msg.author, msg.repo),
                null)
            is Msg.CreateAuthor -> Triple(state.value, null, UiEvent.CreateAuthorDialog)
            is Msg.CreatedAuthor -> Triple(state.value, SideEffect.SaveAuthor(msg.authorDto), null)
            else -> Triple(state.value, null, null)
        }
        _state.value = state
        if (uiEvent != null) {
            _uiEvent.emit(uiEvent)
        }
        actorEffect.send(effect)
    }

    private suspend fun handleEffect(effect: SideEffect?) {
        when (effect) {
            is SideEffect.SelectDirectory -> {
                val file = configureUseCase.handleSelectDirectory(effect.path)
                if (file != null) {
                    handleEffect(SideEffect.ReadConfigFile(file))
                } else {
                    handleEffect(SideEffect.CreateConfigFile(effect.path))
                }
            }
            is SideEffect.CreateConfigFile -> {
                val file = configureUseCase.createConfigFile(effect.path)
                handleEffect(SideEffect.ReadConfigFile(file))
            }
            is SideEffect.ReadConfigFile -> {
                val showDto = configureUseCase.readConfigFile(effect.configFile)
                setShowState(showDto)
            }
            is SideEffect.Fix -> {
                with(effect) {
                    val showDto = fixUseCase.createFix(path, author, repo, arguments)
                    setShowState(showDto)

                }
            }
            is SideEffect.Rollback -> {
                with(effect) {
                    val showDto = rollbackUseCase.makeRollback(path, author, repo)
                    if (showDto != null) {
                        setShowState(showDto)
                    }
                }
            }
            is SideEffect.Reset -> {
                with(effect) {
                    val showDto = resetUseCase.reset(path, author, repo, arguments)
                    if (showDto != null) {
                        setShowState(showDto)
                    }
                }
            }
            is SideEffect.CheckCommand -> {
                if (effect.author == null) actorMsg.send(Msg.CreateAuthor).also { return }
                val commandDto = withContext(Dispatchers.Default) {
                    checkCommandUseCase.checkCommand(effect.commandInput)
                }
                when (commandDto.command) {
                    Command.Fix -> handleEffect(SideEffect.Fix(effect.path,
                        effect.author!!,
                        effect.repo,
                        commandDto.arguments))
                    Command.Rollback -> handleEffect(SideEffect.Rollback(effect.path,
                        effect.author!!,
                        effect.repo))
                    Command.Restart -> actorMsg.send(Msg.Init)
                    Command.Reset -> handleEffect(SideEffect.Reset(effect.path,
                        effect.author!!,
                        effect.repo,
                        commandDto.arguments))
                    else -> _uiEvent.emit(UiEvent.ShowToast("Not valid command"))
                }

            }
            is SideEffect.SaveAuthor -> {
                val authors = authorUseCase.create(effect.authorDto)
                val curState = state.value
                if (curState is State.Show) {
                    _state.value = curState.copy(authors = authors)
                }
            }
        }
    }

    private fun setShowState(showDto: ShowDto) {
        _state.value = State.Show(
            path = showDto.path,
            repo = showDto.repo,
            fixToFiles = showDto.fixToFiles,
            authors = showDto.authors
        )
    }


    fun send(msg: Msg) {
        storeScope.launch {
            actorMsg.send(msg)
        }
    }

    fun onDestroy() = storeScope.cancel()

}




