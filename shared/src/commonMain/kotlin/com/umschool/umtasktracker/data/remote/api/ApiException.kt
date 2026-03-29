package com.umschool.umtasktracker.data.remote.api

sealed class ApiException(message: String) : Exception(message) {
    class Unauthorized : ApiException("Неверный email или пароль")
    class Forbidden : ApiException("Дождитесь подтверждения от руководителя")
    class ServerError(code: Int) : ApiException("Ошибка сервера: $code")
    class NetworkError(cause: Throwable? = null) : ApiException(
        "Нет подключения к интернету" +
            if (cause != null) "\n[${cause::class.simpleName}: ${cause.message}]" else ""
    )
    class EmailAlreadyExists : ApiException("Пользователь с таким email уже существует")
    class ValidationError(details: String) : ApiException(
        "Ошибка валидации" + if (details.isNotBlank()) ": $details" else ""
    )
}
