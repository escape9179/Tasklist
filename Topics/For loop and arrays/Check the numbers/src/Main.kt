fun main() {
    val size = readln().toInt()
    val array = IntArray(size)
    for (i in 0 until size) {
        array[i] = readln().toInt()
    }
    val (numOne, numTwo) = readln().split(" ").map { it.toInt() }
    for (i in 0 until size) {
        if (i + 1 > size - 1) {
            println("YES")
            return
        }
        val first = array[i]
        val second = array[i + 1]
        if (first == numOne && second == numTwo || first == numTwo && second == numOne) {
            println("NO")
            return
        }
    }
    println("YES")
}