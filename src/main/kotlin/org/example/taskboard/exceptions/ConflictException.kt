package org.example.taskboard.exceptions

class ConflictException(override val message: String) : RuntimeException(message)