package org.example.taskboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration

// Will use custom JWT auth, so no need for Spring's UserDetails
@SpringBootApplication(exclude = [ReactiveUserDetailsServiceAutoConfiguration::class])
class TaskBoardApplication

fun main(args: Array<String>) {
    runApplication<TaskBoardApplication>(*args)
}