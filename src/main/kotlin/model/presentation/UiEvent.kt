package model.presentation

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    object ShowFileManager : UiEvent()
    object CreateAuthorDialog : UiEvent()
}

