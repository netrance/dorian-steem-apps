package lee.dorian.dorian_ktx

fun Int.isOdd(): Boolean {
    return when (this % 2) {
        1 -> true
        else -> false
    }
}