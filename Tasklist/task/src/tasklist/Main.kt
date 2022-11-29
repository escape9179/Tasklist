package tasklist

import java.io.File
import kotlin.system.exitProcess

val taskList = mutableListOf<MutableList<String>>()
var taskNumber = 1
fun requestInput() {
    when (readln().lowercase()) {
        "add" -> {
            println("Input the task priority")

            println("Input a new task (enter a blank line to end):")
            // Read tasks from input until they enter a blank line.
            var input = readln().trim()
            if (input.isBlank()) {
                println("The task is blank")
                println("Input an action (add, print, end):")
                return
            }
            while (input.isNotBlank()) {
                if (taskList.size != taskNumber) {
                    taskList.add(mutableListOf(input))
                } else {
                    taskList[taskList.indices.last].add(input)
                }
                input = readln().trim()
            }
            taskNumber++
            println("Input an action (add, print, end):")
            return
        }

        "print" -> {
            if (taskList.isEmpty()) {
                println("No tasks have been input")
                println("Input an action (add, print, end):")
                return
            }
            // Output the task list, prepending a number before each task and using
            // format specifiers to add spaces between the number and task.
            taskList.forEachIndexed { index, taskLines ->
                if (taskLines.size == 1) {
                    println("%-2d %s".format(index + 1, taskLines[0]))
                    println()
                } else {
                    println("%-2d %s".format(index + 1, taskLines[0]))
                    taskLines.subList(1, taskLines.size).forEach { line -> println(line.padStart(line.length + 3)) }
                    println()
                }
            }
            println("Input an action (add, print, end):")
            return
        }

        "end" -> {
            println("Tasklist exiting!")
            exitProcess(0)
        }

        else -> {
            println("The input action is invalid")
            println("Input an action (add, print, end):")
            requestInput()
        }
    }
}

fun main() {
    println("Input an action (add, print, end):")
    while (true) {
        requestInput()
    }
}


