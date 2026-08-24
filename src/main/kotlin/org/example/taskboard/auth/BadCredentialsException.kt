package org.example.taskboard.auth

class BadCredentialsException(override val message: String) : RuntimeException(message)