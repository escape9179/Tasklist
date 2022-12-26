wpackage tasklist

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File
import java.lang.NumberFormatException
import kotlin.system.exitProcess

val taskList = mutableListOf<Task>()
const val YEAR_LENGTH = 4
const val MONTH_LENGTH_MAX = 2
const val DAY_LENGTH_MAX = 2
const val INTRODUCTION_LINE = "Input an action (add, print, edit, delete, end):"
const val NO_TASKS_INPUT_MESSAGE = "No tasks have been input"
const val INVALID_TASK_NUMBER_MESSAGE = "Invalid task number"
const val BG_RED = "\u001B[101m \u001B[0m"
const val BG_YELLOW = "\u001B[103m \u001B[0m"
const val BG_GREEN = "\u001B[102m \u001B[0m"
const val BG_BLUE = "\u001B[104m \u001B[0m"
const val TASK_MAX_LENGTH = 44
const val DATA_FILE_NAME = "tasklist.json"

data class Task(
    var priority: Priority,
    var date: String,
    var time: Time,
    var contents: MutableList<String> = mutableListOf()
) {
    var id = taskList.size + 1

    fun dueTag(): DueTag {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
//        val daysUntil = currentDate.daysUntil(date)
        val daysUntil = currentDate.daysUntil(LocalDate.parse(date))
        return when {
            daysUntil < 0 -> DueTag.OVERDUE
            daysUntil > 0 -> DueTag.IN_TIME
            else -> DueTag.TODAY
        }
    }

    fun print() {
        if (taskList.isEmpty()) {
            println(NO_TASKS_INPUT_MESSAGE)
            println(INTRODUCTION_LINE)
            return
        }

        val chunkedContents = contents.flatMap { it.chunked(TASK_MAX_LENGTH) }
        println(
            "| ${
                id.toString().padEnd(1)
            }  | $date | $time | ${priority.color} | ${dueTag().color} |${chunkedContents[0].padEnd(TASK_MAX_LENGTH)}|"
        )
        chunkedContents.drop(1).forEach {
            println(
                """
        |    |            |       |   |   |${it.padEnd(44)}|
        """.trimIndent()
            )
        }
        println("+----+------------+-------+---+---+--------------------------------------------+")
    }

    enum class Priority(val letter: String, val color: String) {
        CRITICAL("C", BG_RED), HIGH("H", BG_YELLOW), NORMAL("N", BG_GREEN), LOW("L", BG_BLUE)
    }

    enum class DueTag(val letter: String, val color: String) {
        IN_TIME("I", BG_GREEN), TODAY("T", BG_YELLOW), OVERDUE("O", BG_RED)
    }
}

data class Time(val hours: Int, val minutes: Int) {

    override fun toString(): String {
        return "%02d:%02d".format(hours, minutes)
    }
}

fun loadTaskList() {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val taskAdapter = moshi.adapter<MutableList<Task>>(type)
    val dataFile = File(DATA_FILE_NAME)
    if (!dataFile.exists()) {
        dataFile.createNewFile()
        return
    }
    taskList.addAll(taskAdapter.fromJson(dataFile.readText())!!)
}

fun saveTaskList(taskList: MutableList<Task>): Unit {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val taskAdapter = moshi.adapter<MutableList<Task>>(type)
    val dataFile = File(DATA_FILE_NAME)
    dataFile.writeText(taskAdapter.toJson(taskList))
}

fun printTaskListHeader(): Unit {
    println(
        """
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
        """.trimIndent()
    )
}


fun requestDate(): String {
    println("Input the date (yyyy-mm-dd):")
    val input = readln()
    val parts = input.split('-')
    if (parts.size < 3) {
        println("The input date is invalid")
        return requestDate()
    }
    val year = parts[0]
    val month = if (parts[1].length < 2) "0" + parts[1] else parts[1]
    val day = if (parts[2].length < 2) "0" + parts[2] else parts[2]

    return try {
        if (year.length == YEAR_LENGTH && month.length <= MONTH_LENGTH_MAX && day.length <= DAY_LENGTH_MAX
            && year.toIntOrNull() != null && 0 < month.toInt() && month.toInt() <= 12 && 0 < day.toInt() && day.toInt() <= 31
        ) {
            LocalDate (year.toInt(), month.toInt(), day.toInt())
            return "$year-$month-$day"
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
        && parts[0].toInt() <= 23 && parts[1].toInt() < 60
    ) {
        return Time(hours, minutes)
    }
    println("The input time is invalid")
    return requestTime()
}

fun requestPriority(): Task.Priority {
    println("Input the task priority (C, H, N, L):")
    val priority = readln().uppercase()
    if (priority !in Task.Priority.values().map { it.letter }) {
        return requestPriority()
    }
    return Task.Priority.values().first { it.letter == priority }
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
            saveTaskList(taskList)
            println(INTRODUCTION_LINE)
            return
        }

        "print" -> {
            if (taskList.isEmpty()) {
                println(NO_TASKS_INPUT_MESSAGE)
                println(INTRODUCTION_LINE)
                return
            }
            printTaskListHeader()
            taskList.forEach { it.print() }
            println()
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
            printTaskListHeader()
            taskList.forEach { it.print() }
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
            printTaskListHeader()
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

fun main() {
    loadTaskList()
    println(INTRODUCTION_LINE)
    while (true) {
        requestInput()
    }
}


