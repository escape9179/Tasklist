import kotlin.math.min

class City(val name: String) {
    var degrees: Int = 0
        set(value) {
            if (-92 <= value && value <= 57) {
                field = value
            } else {
                field = when (name.lowercase()) {
                    "moscow" ->  5
                    "hanoi" -> 20
                    "dubai" -> 30
                    else -> value
                }
            }
        }
}        

fun main() {
    val first = readLine()!!.toInt()
    val second = readLine()!!.toInt()
    val third = readLine()!!.toInt()
    val firstCity = City("Dubai")
    firstCity.degrees = first
    val secondCity = City("Moscow")
    secondCity.degrees = second
    val thirdCity = City("Hanoi")
    thirdCity.degrees = third

    //implement comparing here
    if (setOf(firstCity.degrees, secondCity.degrees, thirdCity.degrees).size < 3) {
        println("neither")
    } else {
        when (min(firstCity.degrees, min(secondCity.degrees, thirdCity.degrees))) {
            firstCity.degrees -> println(firstCity.name)
            secondCity.degrees -> println(secondCity.name)
            thirdCity.degrees -> println(thirdCity.name)
        }
    }
}