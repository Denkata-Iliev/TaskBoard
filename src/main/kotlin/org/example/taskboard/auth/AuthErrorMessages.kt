package org.example.taskboard.auth

object AuthErrorMessages {
    const val EMAIL_MUST_BE_VALID = "Email must be a valid email address!"
    const val EMAIL_MUST_NOT_BE_EMPTY = "Email must not be empty!"
    const val PASSWORD_MUST_NOT_BE_EMPTY = "Password must not be empty!"
    const val PASSWORD_REQUIREMENTS = "Password has to be at least 8 characters long with at least 1 uppercase, " +
            "1 lowercase letter, 1 number and 1 special character!"
    const val INVALID_EMAIL_OR_PASSWORD = "Invalid email or password!"
    const val EMAIL_ALREADY_EXISTS = "User with email %s already exists!"
}