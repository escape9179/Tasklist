fun main() {
    val size = readln().toInt()
    val array = IntArray(size)
    for (i in 0 until size) {
        array[i] = readln().toInt()
    }
    val copy = array.copyOf()
    for (i in 0 until size) {
        if (i - 1 < 0) {
            array[i] = copy[array.lastIndex]
        } else {
            array[i] = copy[i - 1]
        }
    }
    for (e in array) {
        print("$e ")
    }
}