package tasklist

import kotlinx.datetime.*
import java.lang.NumberFormatException
import java.time.LocalTime
import kotlin.math.min
import kotlin.system.exitProcess
import kotlin.time.Duration

val taskList = mutableListOf<Task>()
var taskNumber = 1
const val YEAR_LENGTH = 4
const val MONTH_LENGTH_MAX = 2
const val DAY_LENGTH_MAX = 2

data class Task(val date: LocalDate, val time: Time, val priority: String, val contents: MutableList<String> = mutableListOf()) {
    val id = lastId
    fun dateAsString(): String {
        return "${date.year}-${date.month}-${date.dayOfMonth}"
    }

    companion object {
        var lastId: Int = 1
    }
}

data class Time(val hours: Int, val minutes: Int) {
    override fun toString(): String {
        return "$hours:$minutes"
    }
}

fun requestDate(): LocalDate {
    println("Input the date (yyyy-mm-dd):")
    val input = readln()
    val parts = input.split('-')
    if (parts.size < 3) {
        println("The input date is invalid")
        return requestDate()
    }
    val year = parts[0]
    val month = if (parts[1].length < 2) "0"+parts[1] else parts[1]
    val day = if (parts[2].length < 2) "0"+parts[2] else parts[2]

    return try {
        if (year.length == YEAR_LENGTH && month.length <= MONTH_LENGTH_MAX && day.length <= DAY_LENGTH_MAX
            && year.toIntOrNull() != null && 0 < month.toInt() && month.toInt() <= 12 && 0 < day.toInt() && day.toInt() <= 31
        ) {
            return LocalDate(year.toInt(), month.toInt(), day.toInt())
        }
        println("The input date is invalid")
        return requestDate()
    } catch (_: NumberFormatException) {
        println("The input date is invalid")
        return requestDate()
    } catch (_: IllegalArgumentException) {
        println("The input date is invalid")
        return requestDate()
    }
}

fun requestTime(): Time {
    println("Input the time (hh:mm):")
    val input = readln()
    val parts = input.split(':')
    if (parts.size != 2) {
        println("The input time is invalid")
        return requestTime()
    }
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()


    if (parts[0].isNotEmpty() && parts[0].length <= 2 && parts[1].isNotEmpty() && parts[1].length <= 2
        && parts[0].toInt() <= 23) {
        return Time(hours, minutes)
    }
    println("The input time is invalid")
    return requestTime()
}

fun requestPriority(): String {
    val taskPriorities = arrayOf("C", "H", "N", "L")
    val priority = readln().uppercase()
    if (priority !in taskPriorities) {
        println("Input the task priority (C, H, N, L):")
        return requestPriority()
    }
    return priority
}

fun requestInput() {
    when (readln().lowercase()) {
        "add" -> {
            println("Input the task priority (C, H, N, L):")
            val priority = requestPriority()
            val task = Task(requestDate(), requestTime(), priority)

            println("Input a new task (enter a blank line to end):")
            var input = readln().trim()
            if (input.isBlank()) {
                println("The task is blank")
                println("Input an action (add, print, end):")
                return
            }
            while (input.isNotBlank()) {
                task.contents.add(input)
                input = readln().trim()
            }
            taskList.add(task)
//            taskNumber++
            Task.lastId++
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
            taskList.forEach { task ->
                println("%-2d %s %s %s".format(task.id, task.date, task.time, task.priority))
                task.contents.forEach { println(it.padStart(it.length + 3))  }
                println()
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
    println("Input an action (add, print, edit, delete, end):")
    while (true) {
        requestInput()
    }
}


