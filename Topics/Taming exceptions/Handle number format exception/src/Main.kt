fun parseCardNumber(cardNumber: String): Long {
    val noSpaces = cardNumber.replace(" ", "")
    val regex = Regex("(([0-9]){4}\\s){3}[0-9]{4}")
    if (regex.matchEntire(cardNumber) == null) {
        throw Exception()
    } else {
        return noSpaces.toLong()
    }
}