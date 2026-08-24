package org.example.taskboard.fixtures

import org.example.taskboard.auth.RegisterRequest
import org.example.taskboard.auth.BoardUser
import org.example.taskboard.auth.LoginRequest
import java.time.Instant
import java.util.UUID

fun aUser(
    id: UUID = UUID.randomUUID(),
    email: String = "mail@mail.com",
    password: String = "hashed_password",
    createDate: Instant = Instant.now()
) = BoardUser(id, email, password, createDate)

fun aRegisterRequest(
    email: String = "mail@mail.com",
    password: String = "Password123#"
) = RegisterRequest(email, password)

fun aLoginRequest(
    email: String = "mail@mail.com",
    password: String = "Password123#"
) = LoginRequest(email, password)