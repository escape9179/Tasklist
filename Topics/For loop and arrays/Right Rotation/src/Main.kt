import kotlin.math.abs

fun main() {
    val n = readln().toInt()
    val array = IntArray(n)
    for (i in 0 until n)
        array[i] = readln().toInt()
    // Get value of rotation
    var r = readln().toInt()
    r=if(r/n>0)r%n else r
    // If the rotation mod the number of elements or
    // the rotation amount is 0 then
    // the elements will be is the same position.
    if(r%n==0||r==0){
        printElements(array)
        return
    }

    val copy = array.copyOf()
    for (i in array.indices) {
        if (r+i>array.lastIndex){
            if(r/n>0)array[r-i]=copy[i]
            else array[abs(n-r-i)]=copy[i]
        }else array[r+i]=copy[i]
    }
    // Print the contents of the array
    printElements(array)
}

fun printElements(array: IntArray){
    for(e in array) print("$e ")
}