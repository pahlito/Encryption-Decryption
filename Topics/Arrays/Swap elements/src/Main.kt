fun main() {    
    val numbers = readLine()!!.split(' ').map { it.toInt() }.toIntArray()
    // Do not touch lines above
    // Write only exchange actions here.
    val first = numbers.first()
    numbers[0] = numbers.last()
    numbers[numbers.lastIndex] = first
    // Do not touch lines below
    println(numbers.joinToString(separator = " "))
}