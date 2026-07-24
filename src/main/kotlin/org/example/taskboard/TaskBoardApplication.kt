package org.example.taskboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskBoardApplication

fun main(args: Array<String>) {
    runApplication<TaskBoardApplication>(*args)
}