package org.example.taskboard.exceptions

import org.example.taskboard.auth.BadCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationExceptions(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .groupBy({ it.field }, { it.defaultMessage ?: "Invalid value" })
            .toSortedMap()
            .map { ValidationError(it.key, it.value) }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                errors = errors
            )
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException) =
        buildGenericErrorResponse(HttpStatus.UNAUTHORIZED, ex.message)

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(ex: ConflictException) =
        buildGenericErrorResponse(HttpStatus.CONFLICT, ex.message)

    private fun buildGenericErrorResponse(status: HttpStatus, message: String?): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                errors = listOf(ValidationError(
                    field = null,
                    fieldErrors = listOf(message ?: "Unknown error")
                ))
            )
        )
    }

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val errors: List<ValidationError>
    )

    data class ValidationError(val field: String?, val fieldErrors: List<String>)
}