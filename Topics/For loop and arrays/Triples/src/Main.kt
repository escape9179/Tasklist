const val sizeOfTriple = 3
fun main() {
    val size = readln().toInt()
    val array = IntArray(size)
    for (i in 0 until size) {
        array[i] = readln().toInt()
    }
    var numOfThirds = 0
    for (i in 0 until size) {
        val first = array[i]
        val third = if (i + 2 > array.lastIndex) break else array[i + 2]
        val second = array[i + 1]
        if (first + 1 == second && second + 1 == third) numOfThirds++
    }
    println(numOfThirds)
}