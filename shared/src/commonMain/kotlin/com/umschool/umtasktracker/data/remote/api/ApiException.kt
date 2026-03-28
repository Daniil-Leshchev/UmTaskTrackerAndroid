package com.umschool.umtasktracker.data.remote.api

// Типизированные сетевые ошибки
sealed class ApiException(message: String) : Exception(message) {
    class Unauthorized : ApiException("Неверный email или пароль")                       // 401
    class Forbidden : ApiException("Дождитесь подтверждения от руководителя")             // 403
    class ServerError(code: Int) : ApiException("Ошибка сервера: $code")                  // 5xx
    class NetworkError(cause: Throwable? = null) : ApiException(
        "Нет подключения к интернету" +
            if (cause != null) "\n[${cause::class.simpleName}: ${cause.message}]" else ""
    )
    class EmailAlreadyExists : ApiException("Пользователь с таким email уже существует")  // 409 / 400
    class ValidationError(details: String) : ApiException(
        "Ошибка валидации" + if (details.isNotBlank()) ": $details" else ""
    )
}
