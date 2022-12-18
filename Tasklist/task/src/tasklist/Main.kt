package tasklist

import kotlinx.datetime.*
import java.lang.NumberFormatException
import kotlin.system.exitProcess

val taskList = mutableListOf<Task>()
const val YEAR_LENGTH = 4
const val MONTH_LENGTH_MAX = 2
const val DAY_LENGTH_MAX = 2
const val INTRODUCTION_LINE = "Input an action (add, print, edit, delete, end):"
const val NO_TASKS_INPUT_MESSAGE = "No tasks have been input"
const val INVALID_TASK_NUMBER_MESSAGE = "Invalid task number"
val currentYear = Clock.System.now().toLocalDateTime(TimeZone.UTC).year

data class Task(var priority: String, var date: LocalDate, var time: Time, var contents: MutableList<String> = mutableListOf()) {
    var id = taskList.size + 1
    fun dateAsString(): String {
        return "${date.year}-${date.month}-${date.dayOfMonth}"
    }

    fun getDueTag(): String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val daysUntil = currentDate.daysUntil(date)
        return when {
            daysUntil < 0 -> "O"
            daysUntil > 0 -> "I"
            else -> "T"
        }
    }

    fun print() {
        if (taskList.isEmpty()) {
            println(NO_TASKS_INPUT_MESSAGE)
            println(INTRODUCTION_LINE)
            return
        }

            println("%-2d %s %s %s %s".format(id, date, time, priority, getDueTag()))
            for (line in contents) {
                println(line.padStart(line.length + 3))
            }
            println()
    }
}

data class Time(val hours: Int, val minutes: Int) {
    override fun toString(): String {
        return "%02d:%02d".format(hours, minutes)
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
        && parts[0].toInt() <= 23 && parts[1].toInt() < 60) {
        return Time(hours, minutes)
    }
    println("The input time is invalid")
    return requestTime()
}

fun requestPriority(): String {
    println("Input the task priority (C, H, N, L):")
    val taskPriorities = arrayOf("C", "H", "N", "L")
    val priority = readln().uppercase()
    if (priority !in taskPriorities) {
        return requestPriority()
    }
    return priority
}

fun requestTask(): MutableList<String> {
    println("Input a new task (enter a blank line to end):")
    val contents = mutableListOf<String>()
    var input = readln().trim()
    if (input.isBlank()) {
        println("The task is blank")
        println(INTRODUCTION_LINE)
    }
    while (input.isNotBlank()) {
        contents.add(input)
        input = readln().trim()
    }
    return contents
}

fun requestInput() {
    when (readln().lowercase()) {
        "add" -> {
            val task = Task(requestPriority(), requestDate(), requestTime(), requestTask())

            taskList.add(task)
//            taskNumber++
            println(INTRODUCTION_LINE)
            return
        }

        "print" -> {
            if (taskList.isEmpty()) {
                println(NO_TASKS_INPUT_MESSAGE)
                println(INTRODUCTION_LINE)
                return
            }
//
//            // Output the task list, prepending a number before each task and using
//            // format specifiers to add spaces between the number and task.
//            taskList.forEach { task ->
////                val taskContents = task.contents.joinToString("\n")
//                println("%-2d %s %s %s %s".format(task.id, task.date, task.time, task.priority, task.getDueTag()))
////                + "\n" + taskContents.padStart(taskContents.length + 3))
//                for (line in task.contents) {
//                    println(line.padStart(line.length + 3))
//                }
////                task.contents.forEach { println(it.padStart(it.length + 3))  }
//                println()
//            }
            taskList.forEach { it.print() }
            println(INTRODUCTION_LINE)
            return
        }

        "end" -> {
            println("Tasklist exiting!")
            exitProcess(0)
        }

        "delete" -> {
            if (taskList.isEmpty()) {
                println(NO_TASKS_INPUT_MESSAGE)
                println(INTRODUCTION_LINE)
                return
            }
            taskList.forEach { it.print() }
//            println("Input the task number (1-${taskList.size}):")
//            val input = readln()
//            if (!isTaskNumber(input)) {
//                println(INVALID_TASK_NUMBER_MESSAGE)
//                requestTaskNumber()
//                return
//            }
            val taskNumber = requestTaskNumber()
            taskList.removeIf { it.id == taskNumber }
            taskList.forEachIndexed { i, task -> task.id = i + 1 }
            println("The task is deleted")
            println(INTRODUCTION_LINE)
        }

        "edit" -> {
            if (taskList.isEmpty()) {
                println(NO_TASKS_INPUT_MESSAGE)
                println(INTRODUCTION_LINE)
                return
            }
            taskList.forEach { it.print() }
            println()
            val taskNumber = requestTaskNumber()
            editTask(taskNumber)
            println(INTRODUCTION_LINE)
        }

        else -> {
            println("The input action is invalid")
            println(INTRODUCTION_LINE)
            requestInput()
        }
    }
}

fun editTask(taskNumber: Int) {
    println("Input a field to edit (priority, date, time, task):")
    when (readln().lowercase()) {
        "priority" -> {
            val priority = requestPriority()
            taskList[taskNumber - 1].priority = priority
        }
        "date" -> {
            val date = requestDate()
            taskList[taskNumber - 1].date = date
        }
        "time" -> {
            val time = requestTime()
            taskList[taskNumber - 1].time = time
        }
        "task" -> {
            val task = requestTask()
            taskList[taskNumber - 1].contents = task
        }
        else -> {
            println("Invalid field")
            return editTask(taskNumber)
        }
    }
    println("The task is changed")
}

fun isTaskNumber(number: String): Boolean {
    return try {
        0 < number.toInt() && number.toInt() <= taskList.size
    } catch (e: NumberFormatException) {
        false
    }
}

fun requestTaskNumber(): Int {
    println("Input the task number (1-${taskList.size}):")
    val taskNumber = readln()
    return if (isTaskNumber(taskNumber)) {
        taskNumber.toInt()
    } else {
        println(INVALID_TASK_NUMBER_MESSAGE)
        requestTaskNumber()
    }
}

fun main() {
    println(INTRODUCTION_LINE)
    while (true) {
        requestInput()
    }
}


